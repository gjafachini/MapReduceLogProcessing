package env;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import log.LogProcessingJob;
import master.Job;
import master.JobExecutionException;
import master.MasterService;
import master.NodePool;
import master.SingleMasterService;
import node.NodeService;
import node.ThreadedNodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dfs.DfsService;
import dfs.LocalDfsService;

public class StartEnv {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartEnv.class);

    public static void main(String[] args) throws JobExecutionException {
        DfsService dfs = new LocalDfsService();
        MasterService master = new SingleMasterService(new NodePool());
        Job job = new LogProcessingJob(dfs, "teste1.txt");

        ExecutorService executor1 = Executors.newFixedThreadPool(1);
        ExecutorService executor2 = Executors.newFixedThreadPool(1);
        NodeService slave1 = new ThreadedNodeService(dfs, executor1);
        NodeService slave2 = new ThreadedNodeService(dfs, executor2);

        master.registerNode(slave1);
        master.registerNode(slave2);

        Collection<String> createdFiles = master.submitJob(job);

        for (String file : createdFiles) {
            LOGGER.info(file);
        }

        // executor1.shutdown();
        // executor2.shutdown();
    }
}
