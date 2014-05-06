package node;

import java.util.List;
import java.util.concurrent.Callable;

import api.Job;

public class NodeReduceTask implements Callable<String> {

    private List<?> mergeResultFiles;
    private final Job job;
    private final String key;

    public NodeReduceTask(Job job, String key, List<?> mergeResultFileName) {
        this.job = job;
        this.key = key;
        this.mergeResultFiles = mergeResultFileName;
    }

    @Override
    public String call() throws Exception {
        // List of values changed for a File reference.
        return this.job.reduce(this.key, mergeResultFiles);
    }

}
