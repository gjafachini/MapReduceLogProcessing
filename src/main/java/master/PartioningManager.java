package master;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;

import api.Job;

/**
 * Map-Reduce steps interface controller.
 */
public interface PartioningManager {
    
    /**
     * Wait future reduce tasks to finish and post outputs.
     * 
     * @param reduceTasks
     * @return reduce output
     */
    Collection<String> assembleOutput(Collection<Future<String>> reduceTasks);
    
    /**
     * Return merge task result.
     * 
     * @param mergeResultsByKey
     * @return merged files map
     */
    Map<String, String> getMergedFileNames(Collection<Future<Map<String, String>>> mergeResultsByKey);
    
    /**
     * @param job, user reduce job
     * @param mergedFileNames
     * @return Reduced future values
     */
    Collection<Future<String>> dispatchReduceTask(Job job, Map<String, String> mergedFileNames);
    
    /**
     * Calls merge task on future mapped tasks.
     * 
     * @param mappingTasks
     * @return future merged maps
     */
    Collection<Future<Map<String, String>>> dispatchMergeTask(Collection<Future<String>> mappingTasks);
    
    /**
     * Calls mapping task.
     * 
     * @param job
     * @return future mapped values.
     */
    Collection<Future<String>> dispatchMappingTask(Job job);
}
