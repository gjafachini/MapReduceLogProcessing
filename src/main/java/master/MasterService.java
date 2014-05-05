package master;

import java.util.Collection;

import api.Job;
import api.JobExecutionException;
import node.NodeService;

public interface MasterService {

    Collection<String> submitJob(Job job) throws JobExecutionException;

    void registerNode(NodeService node);

}
