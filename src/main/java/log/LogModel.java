package log;

import java.util.Date;

/**
 * V Model for mapReduce process and Sorting.
 * 
 * @author Guilherme
 *
 */
public class LogModel {

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

}
