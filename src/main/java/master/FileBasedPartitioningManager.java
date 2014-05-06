package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import node.NodeService;
import api.Job;
import api.JobExecutionException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import dfs.DfsException;
import dfs.DfsService;

public class FileBasedPartitioningManager implements PartioningManager {

    private final DfsService dfs;
    private final NodePool nodePool;

    public FileBasedPartitioningManager(DfsService dfs, NodePool nodePool) {
        this.dfs = dfs;
        this.nodePool = nodePool;
    }

    public Collection<String> assembleOutput(Collection<Future<String>> reduceTaks) {
        Collection<String> outputFileNames = Lists.newArrayList();
        // Wait for reduce tasks completion.
        for (Future<String> reduceTask : reduceTaks) {
            if (reduceTask.isDone()) {
                try {
                    outputFileNames.add(reduceTask.get());
                } catch (InterruptedException | ExecutionException e) {
                    throw new JobExecutionException("Reducing task execution error.", e);
                }
            }
        }
        return outputFileNames;
    }

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

    public Collection<Future<String>> dispatchReduceTask(Job job, Map<String, String> mergedFileNames) {
        Collection<Future<String>> reduceTaks = Lists.newArrayList();
        for (String key : mergedFileNames.keySet()) {
            NodeService reduceNode = this.nodePool.nextIdleNode();
            Future<String> reduceTask = reduceNode.reduce(job, key, Lists.newArrayList(mergedFileNames.get(key)));
            reduceTaks.add(reduceTask);
        }
        return reduceTaks;
    }

    public Collection<Future<Map<String, String>>> dispatchMergeTask(Collection<Future<String>> mappingTasks) {
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

    public Collection<Future<String>> dispatchMappingTask(Job job) {
        Collection<Future<String>> dispatchedTasks = Lists.newArrayList();
        for (String inputFileName : job.getInputs()) {
            NodeService node = this.nodePool.nextIdleNode();
            Future<String> mappingTask = node.map(job, inputFileName);
            dispatchedTasks.add(mappingTask);
        }
        return dispatchedTasks;
    }
}
