package env;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import log.LogParser;
import log.LogProcessingJob;
import master.FileBasedPartitioningManager;
import master.MasterService;
import master.NodePool;
import master.PartioningManager;
import master.SingleMasterService;
import node.NodeService;
import node.ThreadedNodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.Job;
import api.JobExecutionException;
import dfs.DfsException;
import dfs.DfsService;
import dfs.LocalDfsService;

public class StartEnv {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StartEnv.class);
    
    public static void main(String[] args) throws JobExecutionException, DfsException {
        // String[] files = new String[] { "server1/teste.txt", "server2/teste.txt",
        // "server3/teste.txt", "server4/teste.txt" };
        String[] files = new String[] { "server1/teste1.txt", "server2/teste1.txt", "server3/teste1.txt", "server4/teste1.txt" };
        
        DfsService dfs = new LocalDfsService();
        LogParser parser = new LogParser();
        Job job = new LogProcessingJob(parser, files);
        NodePool nodePool = new NodePool();
        PartioningManager manager = new FileBasedPartitioningManager(dfs, nodePool);
        MasterService master = new SingleMasterService(manager, nodePool);
        
        ExecutorService executor1 = Executors.newFixedThreadPool(1);
        ExecutorService executor2 = Executors.newFixedThreadPool(1);
        ExecutorService executor3 = Executors.newFixedThreadPool(1);
        NodeService slave1 = new ThreadedNodeService(dfs, executor1);
        NodeService slave2 = new ThreadedNodeService(dfs, executor2);
        NodeService slave3 = new ThreadedNodeService(dfs, executor3);
        
        master.registerNode(slave1);
        master.registerNode(slave2);
        master.registerNode(slave3);
        
        Collection<String> createdFiles = master.submitJob(job);
        
        for (String file : createdFiles) {
            LOGGER.info("Output file created {}", file);
            File outputFile = dfs.load(file);
            dfs.moveFileTo(outputFile, "server1/temp/");
        }
        executor1.shutdown();
        executor2.shutdown();
        executor3.shutdown();
    }
}
