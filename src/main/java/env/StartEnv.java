package env;

import java.util.concurrent.Executors;

import log.LogProcessingJob;
import master.Job;
import master.JobExecutionException;
import master.MasterService;
import master.NodePool;
import master.SingleMasterService;
import node.NodeService;
import node.ThreadedNodeService;
import dfs.DfsService;
import dfs.LocalDfsService;

public class StartEnv {

    public static void main(String[] args) throws JobExecutionException {
        DfsService dfs = new LocalDfsService();
        MasterService master = new SingleMasterService(new NodePool());
        Job job = new LogProcessingJob(dfs, "C:/workspace/MapReduceLog/ddfs/teste1.txt");

        NodeService slave1 = new ThreadedNodeService(dfs, Executors.newFixedThreadPool(1));
        NodeService slave2 = new ThreadedNodeService(dfs, Executors.newFixedThreadPool(1));

        master.registerNode(slave1);
        master.registerNode(slave2);

        master.submitJob(job);
    }
}
