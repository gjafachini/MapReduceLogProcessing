package com.mapreduce.master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mapreduce.api.Job;
import com.mapreduce.api.JobExecutionException;
import com.mapreduce.dfs.DfsException;
import com.mapreduce.dfs.DfsService;
import com.mapreduce.node.NodeService;

public class FileBasedPartitioningManager implements PartioningManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedPartitioningManager.class);
    
    private final DfsService dfs;
    private final NodePool nodePool;
    
    public FileBasedPartitioningManager(DfsService dfs, NodePool nodePool) {
        this.dfs = dfs;
        this.nodePool = nodePool;
    }
    
    @Override
    public Collection<String> assembleOutput(Collection<Future<String>> reduceTaks) {
        LOGGER.debug("Assembling output files");
        Collection<String> outputFileNames = Lists.newArrayList();
        // Wait for reduce tasks completion.
        for (Future<String> reduceTask : reduceTaks) {
            try {
                outputFileNames.add(reduceTask.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new JobExecutionException("Reducing task execution error.", e);
            }
        }
        return outputFileNames;
    }
    
    @Override
    public Map<String, String> getMergedFileNames(Collection<Future<Map<String, String>>> mergeResultsByKey) {
        // Wait for all merge tasks to complete.
        Multimap<String, String> fileNamesByKeyToBeMerged = ArrayListMultimap.create();
        for (Future<Map<String, String>> mergeByKey : mergeResultsByKey) {
            try {
                for (String key : mergeByKey.get().keySet()) {
                    fileNamesByKeyToBeMerged.put(key, mergeByKey.get().get(key));
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new JobExecutionException("Error merging files", e);
            }
        }
        // Merge files by key.
        Map<String, String> mergedFileNames = Maps.newHashMap();
        for (String key : fileNamesByKeyToBeMerged.keySet()) {
            try {
                String mergedFileName = this.dfs.mergeFiles(fileNamesByKeyToBeMerged.get(key));
                mergedFileNames.put(key, mergedFileName);
            } catch (DfsException e) {
                throw new JobExecutionException("Error merging files", e);
            }
        }
        return mergedFileNames;
    }
    
    @Override
    public Collection<Future<String>> dispatchReduceTask(Job job, Map<String, String> mergedFileNames) {
        LOGGER.debug("Dispatching reducing task for job {}", job);
        Collection<Future<String>> reduceTaks = Lists.newArrayList();
        for (String key : mergedFileNames.keySet()) {
            String mergedFileName = mergedFileNames.get(key);
            NodeService reduceNode = this.nodePool.nextIdleNode();
            Future<String> reduceTask = reduceNode.reduce(job, key, mergedFileName);
            reduceTaks.add(reduceTask);
        }
        return reduceTaks;
    }
    
    @Override
    public Collection<Future<Map<String, String>>> dispatchMergeTask(Collection<Future<String>> mappingTasks) {
        LOGGER.debug("Dispatching merging task");
        Collection<Future<Map<String, String>>> mergeResultsByKey = Lists.newArrayList();
        while (!mappingTasks.isEmpty()) {
            Collection<Future<String>> readyTasks = Lists.newArrayList();
            
            for (Future<String> mappingTask : mappingTasks) {
                if (mappingTask.isDone()) {
                    readyTasks.add(mappingTask);
                    NodeService node = this.nodePool.nextIdleNode();
                    
                    try {
                        mergeResultsByKey.add(node.shuffle(mappingTask.get()));
                    } catch (InterruptedException | ExecutionException e) {
                        throw new JobExecutionException("Mapping task execution error.", e);
                    }
                }
            }
            mappingTasks.removeAll(readyTasks);
            readyTasks.clear();
        }
        return mergeResultsByKey;
    }
    
    @Override
    public Collection<Future<String>> dispatchMappingTask(Job job) {
        LOGGER.debug("Dispatching mapping task for job {}", job);
        Collection<Future<String>> dispatchedTasks = Lists.newArrayList();
        for (String inputFileName : job.getInputs()) {
            NodeService node = this.nodePool.nextIdleNode();
            Future<String> mappingTask = node.map(job, inputFileName);
            dispatchedTasks.add(mappingTask);
        }
        return dispatchedTasks;
    }
}
