package api;

public class MappingResult<T> {

    private final String key;
    private final T value;

    public MappingResult(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }
}
