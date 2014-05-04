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

import com.google.gson.Gson;

import dfs.DfsService;
import dfs.LocalDfsService;

public class StartEnv {

    public static void main(String[] args) throws JobExecutionException {
        DfsService dfs = new LocalDfsService();
        MasterService master = new SingleMasterService(new NodePool(), dfs);
        Job job = new LogProcessingJob();
        Gson gson = new Gson();

        NodeService slave1 = new ThreadedNodeService(dfs, Executors.newFixedThreadPool(1), gson);
        NodeService slave2 = new ThreadedNodeService(dfs, Executors.newFixedThreadPool(1), gson);

        master.registerNode(slave1);
        master.registerNode(slave2);

        master.submitJob(job);
    }
}
