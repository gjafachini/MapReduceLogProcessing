package log;

import java.util.Date;

/**
 * Log Model for log files Sorting.
 * 
 *
 */
public class LogModel implements Comparable<LogModel> {

    private final Date date;
    private final String log;

    public LogModel(Date timestamp, String log) {
        this.date = timestamp;
        this.log = log;
    }

    public Date getDate() {
        return date;
    }

    public String getLog() {
        return log;
    }

    @Override
    public String toString() {
        return "LogModel [date=" + date + ", log=" + log + "]";
    }

    @Override
    public int compareTo(LogModel otherDate) {
        return this.getDate().compareTo(otherDate.getDate());
    }

}
