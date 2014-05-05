package master;

import java.io.File;
import java.util.Collection;

public interface Job {

    MappingResult<?> map(String line);

    Collection<String> getInputs();

    String reduce(String key, File reduceFile) throws JobExecutionException;

}
