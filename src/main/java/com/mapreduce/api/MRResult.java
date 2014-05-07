package com.mapreduce.api;

public class MRResult<T> {

    private final String key;
    private final T value;

    public MRResult(String key, T value) {
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
