package node;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import api.Job;
import dfs.DfsService;

public class ThreadedNodeService implements NodeService {

    private final DfsService dfs;
    private final ExecutorService executor;
    private Future<?> idleChecker;

    public ThreadedNodeService(DfsService dfs, ExecutorService executor) {
        this.dfs = dfs;
        this.executor = executor;
    }

    @Override
    public Future<String> map(final Job job, final String input) {
        Future<String> futureMappedFile = this.executor.submit(new NodeMapTask(this.dfs, job, input));
        idleChecker = futureMappedFile;
        return futureMappedFile;
    }

    @Override
    public Future<Map<String, String>> shuffle(String mapResultFileName) {
        Future<Map<String, String>> futureShuffledFiles = this.executor.submit(new NodeMergeTask(this.dfs, mapResultFileName));
        idleChecker = futureShuffledFiles;
        return futureShuffledFiles;
    }

    @Override
    public Future<String> reduce(final Job job, final String key, final List<String> mergeResultFileName) {
        Future<String> futureReduced = this.executor.submit(new NodeReduceTask(job, key, mergeResultFileName));
        idleChecker = futureReduced;
        return futureReduced;
    }

    @Override
    public boolean isIdle() {
        if (idleChecker == null) {
            return true;
        }
        return idleChecker.isDone();
    }

}
