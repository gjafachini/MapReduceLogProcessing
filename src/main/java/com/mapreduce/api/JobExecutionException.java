package com.mapreduce.api;

public class JobExecutionException extends RuntimeException {
    private static final long serialVersionUID = -6943355641415869466L;
    
    public JobExecutionException(String message, Exception e) {
        super(message, e);
    }
    
}
