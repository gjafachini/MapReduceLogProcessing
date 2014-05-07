package com.mapreduce.node;

import java.util.Map;
import java.util.concurrent.Future;

import com.mapreduce.api.Job;

/**
 * Map-Reduce running unit.
 */
public interface NodeService {
    
    /**
     * Call user job map process on the input.
     * 
     * @param job a user job.
     * @param input given data
     * @return future result of mapping process
     */
    Future<String> map(Job job, String input);
    
    /**
     * Shuffle process for arranging data into one Id each.
     * 
     * @param mappingResultFileName file to shuffle
     * @return Map of K V shuffled from splittingFileName
     * @throws NodeServiceException
     */
    Future<Map<String, String>> shuffle(String mappingResultFileName);
    
    /**
     * Call user job reduce process on the shuffled data for the given key.
     * 
     * @param job a user job.
     * @param key being processed
     * @param mergedResultFileName
     * @return future result of reduce process.
     */
    Future<String> reduce(Job job, String key, String mergedFileName);
    
    /**
     * @return true if node is not processing anything.
     */
    boolean isIdle();
    
}
