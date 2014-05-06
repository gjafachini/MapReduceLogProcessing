package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import node.NodeService;
import api.Job;
import api.JobExecutionException;

public class SingleMasterService implements MasterService {
    
    private final NodePool nodePool;
    private final PartioningManager manager;
    
    public SingleMasterService(PartioningManager manager, NodePool nodePool) {
        this.manager = manager;
        this.nodePool = nodePool;
    }
    
    @Override
    public Collection<String> submitJob(Job job) throws JobExecutionException {
        Collection<Future<String>> mappingTasks = this.manager.dispatchMappingTask(job);
        Collection<Future<Map<String, String>>> mergeResultsByKey = this.manager.dispatchMergeTask(mappingTasks);
        Map<String, String> mergedFileNames = this.manager.getMergedFileNames(mergeResultsByKey);
        Collection<Future<String>> reduceTaks = this.manager.dispatchReduceTask(job, mergedFileNames);
        
        return this.manager.assembleOutput(reduceTaks);
    }
    
    @Override
    public void registerNode(NodeService node) {
        this.nodePool.add(node);
    }
    
}
