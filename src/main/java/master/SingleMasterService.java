package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import node.NodeService;
import node.NodeServiceException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class SingleMasterService implements MasterService {

    private NodePool nodePool;

    public SingleMasterService(NodePool nodePool) {
        this.nodePool = nodePool;
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
                    } catch (InterruptedException | ExecutionException | NodeServiceException e) {
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

        for (String key : reducerMap.keySet()) {
            NodeService reduceNode = nodePool.nextIdleNode();
            FutureTask<String> reduceOutput = reduceNode.reduce(job, key, reducerMap.get(key));
            reduceList.add(reduceOutput);
        }

        while (!reduceList.isEmpty()) {
            Collection<FutureTask<String>> readyTasks = Lists.newArrayList();

            for (FutureTask<String> reduceFileName : reduceList) {
                if (reduceFileName.isDone()) {
                    try {
                        outputFiles.add(reduceFileName.get());
                        readyTasks.add(reduceFileName);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new JobExecutionException("Reducing task execution error.", e);
                    }
                }
            }

            reduceList.removeAll(readyTasks);
            readyTasks.clear();
        }

        return outputFiles;
    }

    @Override
    public void registerNode(NodeService node) {
        nodePool.add(node);
    }

}
