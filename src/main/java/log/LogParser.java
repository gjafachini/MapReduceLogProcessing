package log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import api.JobExecutionException;
import api.MappingResult;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LogParser {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
    private static Gson gson = new Gson();

    public static LogModel parseLog(String log) throws JobExecutionException {
        Date date = new Date();
        String timeString = "";
        String logValue = "";
        try {
            MappingResult<?> mapedData = gson.fromJson(log, MappingResult.class);
            if (mapedData != null) {
                logValue = (String) mapedData.getValue();
                timeString = StringUtils.substringBetween(logValue, "[", "]");
            }
        } catch (JsonSyntaxException e) {
            // nothing
        }

        try {
            if (!timeString.isEmpty()) {
                date = DATE_FORMAT.parse(timeString);
            }
        } catch (ParseException e) {
            throw new JobExecutionException("Error creating LogModel", e);
        }

        return new LogModel(date, logValue);
    }

}
