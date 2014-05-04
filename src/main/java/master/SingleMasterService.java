package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import node.NodeService;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import dfs.DfsException;
import dfs.DfsService;

public class SingleMasterService implements MasterService {

    private NodePool nodePool;
    private DfsService dfs;

    public SingleMasterService(NodePool nodePool, DfsService dfs) {
        this.nodePool = nodePool;
        this.dfs = dfs;
    }

    @Override
    public Collection<String> submitJob(Job job) throws JobExecutionException {
        Collection<FutureTask<String>> splitingList = Lists.newArrayList();
        Collection<FutureTask<String>> reduceList = Lists.newArrayList();
        Multimap<String, String> reducerMap = ArrayListMultimap.create();
        Collection<String> outputFiles = Lists.newArrayList();

        for (String input : job.getInputs()) {
            NodeService newNode = nodePool.nextIdleNode();
            FutureTask<String> mapFileName = newNode.map(job, input);

            splitingList.add(mapFileName);
        }

        while (!splitingList.isEmpty()) {
            Collection<FutureTask<String>> readyTasks = Lists.newArrayList();

            for (FutureTask<String> splittingFileName : splitingList) {
                if (splittingFileName.isDone()) {
                    readyTasks.add(splittingFileName);
                    NodeService shufflingNode = nodePool.nextIdleNode();
                    Map<String, String> shufflingMap;

                    try {
                        shufflingMap = shufflingNode.shuffle(splittingFileName.get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new JobExecutionException("Mapping task execution error.", e);
                    }

                    for (String key : shufflingMap.keySet()) {
                        reducerMap.put(key, shufflingMap.get(key));
                    }
                }
            }

            splitingList.removeAll(readyTasks);
            readyTasks.clear();
        }

        for (String key : reducerMap.keys()) {
            String mergedFileName;
            try {
                mergedFileName = dfs.mergeFiles(reducerMap.get(key));
            } catch (DfsException e) {
                throw new JobExecutionException("Shuffling task error", e);
            }

            NodeService reduceNode = nodePool.nextIdleNode();
            FutureTask<String> reduceOutput = reduceNode.reduce(job, mergedFileName);
            reduceList.add(reduceOutput);
        }

        for (FutureTask<String> reduceFileName : reduceList) {
            try {
                outputFiles.add(reduceFileName.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new JobExecutionException("Reducing task execution error.", e);
            }
        }

        return outputFiles;
    }

    @Override
    public void registerNode(NodeService node) {
        nodePool.add(node);
    }

}
