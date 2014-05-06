package api;

import java.util.Collection;
import java.util.List;

/**
 * User job interface, it would run on a node.
 *
 */
public interface Job {

    /**
     * User map process.
     * @param line to map.
     * @return Key, Value from mapping process.
     */
    MappingResult<?> map(String line);

    /**
     * Return the user given inputs for processing.
     * @return Inputs from user.
     */
    Collection<String> getInputs();

    /**
     * User reduce process.
     * @param key being reduced.
     * @param mergeResultFileName list of files of the same key for reducing.
     * @return reduced values.
     * @throws JobExecutionException
     */
    String reduce(String key, List<?> mergeResultFileName) throws JobExecutionException;

}
