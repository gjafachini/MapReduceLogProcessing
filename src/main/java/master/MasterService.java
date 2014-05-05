package master;

import java.util.Collection;

import node.NodeService;

/**
 * Map-reduce arranger.
 *
 */
public interface MasterService {

    /**
     * Submit user jobs to processing nodes.
     * 
     * @param job
     *            User job.
     * @return job result.
     * @throws JobExecutionException
     */
    Collection<String> submitJob(Job job) throws JobExecutionException;

    /**
     * Register processing units.
     * 
     * @param node
     *            a processing unit.
     */
    void registerNode(NodeService node);

}
