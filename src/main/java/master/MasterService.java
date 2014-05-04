package master;

import java.util.Collection;

import node.NodeService;

public interface MasterService {

    Collection<String> submitJob(Job job) throws JobExecutionException;

    void registerNode(NodeService node);

}
