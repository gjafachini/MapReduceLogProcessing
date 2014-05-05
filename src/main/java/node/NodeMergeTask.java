package node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import api.MappingResult;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

import dfs.DfsException;
import dfs.DfsService;

public class NodeMergeTask implements Callable<Map<String, String>> {
    
    private static final String NEW_LINE = "\n";
    private final DfsService dfs;
    private final String mapResultFileName;
    private final Gson gson = new Gson();
    
    public NodeMergeTask(DfsService dfs, String mapResultFileName) {
        this.dfs = dfs;
        this.mapResultFileName = mapResultFileName;
    }
    
    @Override
    public Map<String, String> call() throws Exception {
        Map<String, String> mergedFilesNames = new HashMap<String, String>();
        File mappedFile;
        
        try {
            mappedFile = this.dfs.load(this.mapResultFileName);
        } catch (DfsException e) {
            throw new NodeServiceException("Error loading mapped file on shuffling", e);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(mappedFile))) {
            Multimap<String, String> shuffledData = ArrayListMultimap.create();
            String line;
            
            while ((line = reader.readLine()) != null) {
                MappingResult<?> mapedData = this.gson.fromJson(line, MappingResult.class);
                
                shuffledData.put(mapedData.getKey(), line);
            }
            
            for (String key : shuffledData.keySet()) {
                StringBuilder fileContent = new StringBuilder();
                Collection<String> oneKeyData = shuffledData.get(key);
                String filename = key + "_" + UUID.randomUUID();
                
                for (String dataLine : oneKeyData) {
                    fileContent.append(dataLine).append(NEW_LINE);
                }
                
                try {
                    this.dfs.save(filename, fileContent.toString());
                    mergedFilesNames.put(key, filename);
                } catch (DfsException e) {
                    throw new NodeServiceException("Error saving shuffled data", e);
                }
            }
            
        } catch (IOException e) {
            throw new NodeServiceException("Error reading mapped file on shuffling", e);
        }
        return mergedFilesNames;
    }
}
