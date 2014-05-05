package log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import master.JobExecutionException;

import org.apache.commons.lang3.StringUtils;

public class LogParser {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");

    public static LogModel parseLog(String log) throws JobExecutionException {
        Date date = new Date();
        String timeString = StringUtils.substringBetween(log, "[", "]");

        try {
            date = DATE_FORMAT.parse(timeString);
        } catch (ParseException e) {
            throw new JobExecutionException("Error creating LogModel", e);
        }

        return new LogModel(date, log);
    }

}
