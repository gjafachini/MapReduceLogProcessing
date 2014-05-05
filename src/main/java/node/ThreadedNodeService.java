package node;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import api.Job;
import dfs.DfsService;

public class ThreadedNodeService implements NodeService {
    
    protected static final Object NEW_LINE = "\n";
    private final DfsService dfs;
    private final ExecutorService executor;
    private boolean isIdle;
    
    public ThreadedNodeService(DfsService dfs, ExecutorService executor) {
        this.dfs = dfs;
        this.executor = executor;
        setIdle(true);
    }
    
    @Override
    public Future<String> map(final Job job, final String input) {
        setIdle(false);
        return this.executor.submit(new NodeMapTask(this.dfs, job, input));
    }
    
    @Override
    public Future<Map<String, String>> shuffle(String mapResultFileName) {
        setIdle(false);
        return this.executor.submit(new NodeMergeTask(this.dfs, mapResultFileName));
    }
    
    @Override
    public Future<String> reduce(final Job job, final String key, final String mergeResultFileName) {
        setIdle(false);
        return this.executor.submit(new NodeReduceTask(this.dfs, job, key, mergeResultFileName));
    }
    
    @Override
    public synchronized boolean isIdle() {
        return this.isIdle;
    }
    
    private synchronized void setIdle(boolean isIdle) {
        this.isIdle = isIdle;
    }
}
