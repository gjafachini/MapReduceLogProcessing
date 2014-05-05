package master;

import java.util.Collection;

public interface Job {

    MappingResult<?> map(String line);

    Collection<String> getInputs();

    String reduce(String key, Collection<?> shuffledList) throws JobExecutionException;

}
