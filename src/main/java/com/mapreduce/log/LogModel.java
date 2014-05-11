package com.mapreduce.log;

import java.util.Date;

/**
 * Log Model for log files Sorting.
 */
public class LogModel implements Comparable<LogModel> {
    
    private final String userId;
    private final Date date;
    private final String log;
    
    public LogModel(String userId, Date timestamp, String log) {
        this.userId = userId;
        this.date = timestamp;
        this.log = log;
    }
    
    public Date getDate() {
        return this.date;
    }
    
    public String getLog() {
        return this.log;
    }
    
    @Override
    public String toString() {
        return "LogModel [userid=" + this.userId + ", date=" + this.date + ", log=" + this.log + "]";
    }
    
    @Override
    public int compareTo(LogModel otherDate) {
        return getDate().compareTo(otherDate.getDate());
    }
    
}
