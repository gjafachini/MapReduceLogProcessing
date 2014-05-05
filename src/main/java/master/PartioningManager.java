package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import api.Job;

public interface PartioningManager {
    
    Collection<String> assembleOutput(Collection<Future<String>> reduceTaks);
    
    Map<String, String> getMergedFileNames(Collection<Future<Map<String, String>>> mergeResultsByKey);
    
    Collection<Future<String>> dispatchReduceTask(Job job, Map<String, String> mergedFileNames);
    
    Collection<Future<Map<String, String>>> dispatchMergeTask(Collection<Future<String>> mappingTasks);
    
    Collection<Future<String>> dispatchMappingTask(Job job);
}
