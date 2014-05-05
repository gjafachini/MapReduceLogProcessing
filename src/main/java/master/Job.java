package master;

import java.io.BufferedReader;
import java.util.Collection;

public interface Job {

    MappingResult<?> map(String line);

    Collection<String> getInputs();

    String reduce(String key, BufferedReader reader);

}
