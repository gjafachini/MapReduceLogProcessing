package master;

import java.util.Collection;

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
     * @param shuffledList processed list for reducing.
     * @return reduced values.
     * @throws JobExecutionException
     */
    String reduce(String key, Collection<?> shuffledList) throws JobExecutionException;

}
