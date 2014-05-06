package api;

import java.util.Collection;

public interface Job {
    
    Collection<String> getInputs();
    
    MRResult<?> map(String line);
    
    MRResult<?> reduce(String key, Collection<MRResult<?>> allKeyValues);
    
}
