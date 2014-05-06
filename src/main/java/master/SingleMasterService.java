package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import node.NodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.Job;
import api.JobExecutionException;

public class SingleMasterService implements MasterService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleMasterService.class);
    
    private final NodePool nodePool;
    private final PartioningManager manager;
    
    public SingleMasterService(PartioningManager manager, NodePool nodePool) {
        this.manager = manager;
        this.nodePool = nodePool;
    }
    
    @Override
    public Collection<String> submitJob(Job job) throws JobExecutionException {
        LOGGER.debug("Submitting job {}", job);
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
