package log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import api.JobExecutionException;
import api.MappingResult;

import com.google.gson.Gson;

public class LogParser {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
    private static Gson gson = new Gson();

    public static LogModel parseLog(String log) throws JobExecutionException {
        Date date = new Date();
        MappingResult<?> mapedData = gson.fromJson(log, MappingResult.class);
        String logValue = (String) mapedData.getValue();
        String timeString = StringUtils.substringBetween(logValue, "[", "]");

        try {
            date = DATE_FORMAT.parse(timeString);
        } catch (ParseException e) {
            throw new JobExecutionException("Error creating LogModel", e);
        }

        return new LogModel(date, logValue);
    }

}
