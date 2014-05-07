package com.mapreduce.dfs;

public class DfsException extends Exception {
    private static final long serialVersionUID = -7397903519884750084L;

    public DfsException(String message, Throwable e) {
        super(message, e);
    }

    public DfsException(String message) {
        super(message);
    }

}
