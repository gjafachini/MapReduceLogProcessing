package node;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import api.Job;
import dfs.DfsService;

public class ThreadedNodeService implements NodeService {
    
    private final DfsService dfs;
    private final ExecutorService executor;
    private volatile Future<?> idleChecker;
    
    public ThreadedNodeService(DfsService dfs, ExecutorService executor) {
        this.dfs = dfs;
        this.executor = executor;
    }
    
    @Override
    public Future<String> map(final Job job, final String input) {
        NodeMapTask task = new NodeMapTask(this.dfs, job, input);
        Future<String> futureMappedFile = this.executor.submit(task);
        this.idleChecker = futureMappedFile;
        return futureMappedFile;
    }
    
    @Override
    public Future<Map<String, String>> shuffle(String mapResultFileName) {
        NodeMergeTask task = new NodeMergeTask(this.dfs, mapResultFileName);
        Future<Map<String, String>> futureShuffledFiles = this.executor.submit(task);
        this.idleChecker = futureShuffledFiles;
        return futureShuffledFiles;
    }
    
    @Override
    public Future<String> reduce(Job job, String key, String mergeResultFileName) {
        NodeReduceTask task = new NodeReduceTask(this.dfs, job, key, mergeResultFileName);
        Future<String> futureReduced = this.executor.submit(task);
        this.idleChecker = futureReduced;
        return futureReduced;
    }
    
    @Override
    public synchronized boolean isIdle() {
        if (this.idleChecker == null) { return true; }
        return this.idleChecker.isDone();
    }
    
}
