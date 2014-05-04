package master;

import java.util.List;

public interface Job {

    MappingResult<?> map(String line);

    List<String> getInputs();

    void reduce(String key, List<String> values);

}
