package api;

import java.util.Collection;

public interface Job {
    
    Collection<String> getInputs();
    
    MappingResult<?> map(String line);
    
    String reduce(String key, Collection<MappingResult<?>> allKeyValues);
    
}
