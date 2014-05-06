package node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;

import api.Job;
import api.MRResult;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import dfs.DfsService;

public class NodeReduceTask implements Callable<String> {
    
    private final DfsService dfs;
    private final String mergeResultFileName;
    private final Job job;
    private final Gson gson = new Gson();
    private final String key;
    
    public NodeReduceTask(DfsService dfs, Job job, String key, String mergeResultFileName) {
        this.dfs = dfs;
        this.job = job;
        this.key = key;
        this.mergeResultFileName = mergeResultFileName;
    }
    
    @Override
    public String call() throws Exception {
        Collection<MRResult<?>> allKeyValues = Lists.newArrayList();
        File mergedFile = this.dfs.load(this.mergeResultFileName);
        String line;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(mergedFile))) {
            while ((line = reader.readLine()) != null) {
                MRResult<?> value = this.gson.fromJson(line, MRResult.class);
                allKeyValues.add(value);
            }
        }
        // List of values changed for a File reference.
        MRResult<?> result = this.job.reduce(this.key, allKeyValues);
        String json = this.gson.toJson(result);
        String reduceResultFileName = UUID.randomUUID().toString();
        
        this.dfs.save(reduceResultFileName, json);
        
        return reduceResultFileName;
    }
    
}
