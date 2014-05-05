package node;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.FutureTask;

import master.Job;

/**
 * Map-Reduce running unit.
 *
 */
public interface NodeService {

    /**
     * Call user job map process on the input.
     * @param job a user job.
     * @param input given data
     * @return future result of mapping process 
     */
    FutureTask<String> map(Job job, String input);

    /**
     * Shuffle process for arranging data into one Id each.
     * @param splittingFileName file to shuffle
     * @return Map of K V shuffled from splittingFileName
     * @throws NodeServiceException
     */
    Map<String, String> shuffle(String splittingFileName) throws NodeServiceException;

    /**
     * Verify is this node is Idle for next process.
     * @return is node Idle
     */
    boolean isIdle();

    /**
     * Call user job reduce process on the shuffled data for the given key.
     * @param job a user job.
     * @param key being processed
     * @param collection shuffled data
     * @return future result of reduce process.
     */
    FutureTask<String> reduce(Job job, String key, Collection<?> collection);

}
