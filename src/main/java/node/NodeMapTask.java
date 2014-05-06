package node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.Job;
import api.MRResult;

import com.google.gson.Gson;

import dfs.DfsService;

public class NodeMapTask implements Callable<String> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeMapTask.class);
    
    private static final String NEW_LINE = "\n";
    private final DfsService dfs;
    private final String inputFileName;
    private final Job job;
    private final Gson gson = new Gson();
    
    public NodeMapTask(DfsService dfs, Job job, String inputFileName) {
        this.dfs = dfs;
        this.job = job;
        this.inputFileName = inputFileName;
    }
    
    @Override
    public String call() throws Exception {
        LOGGER.debug("Executing mapping task");
        File mapFile = this.dfs.load(this.inputFileName);
        String mappingResultFileName = UUID.randomUUID().toString();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
            String line;
            StringBuilder mappedLines = new StringBuilder();
            
            while ((line = reader.readLine()) != null) {
                LOGGER.debug("Reading file line [{}]", line);
                MRResult<?> result = this.job.map(line);
                String json = this.gson.toJson(result);
                
                mappedLines.append(json).append(NEW_LINE);
            }
            this.dfs.save(mappingResultFileName, mappedLines.toString());
        }
        return mappingResultFileName;
    }
    
}
