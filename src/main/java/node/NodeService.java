package node;

import java.util.Map;
import java.util.concurrent.Future;

import api.Job;

public interface NodeService {
    
    Future<String> map(Job job, String inputFileName);
    
    Future<Map<String, String>> shuffle(String mappingResultFileName);
    
    Future<String> reduce(Job job, String key, String mergedResultFileName);
    
    boolean isIdle();
    
}
