package com.mapreduce.log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class LogParser {
    
    private final static String USER_ID_PATTERN = "userid=";
    
    public LogModel parse(String key, Object rawValue) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
        String value = (String) rawValue;
        String timeString = StringUtils.substringBetween(value, "[", "]");
        Date date = formatter.parse(timeString);
        return new LogModel(key, date, value);
    }
    
    public String parseUserId(String log) {
        String userid = "";
        
        if (log.contains(USER_ID_PATTERN)) {
            userid = StringUtils.substringBetween(log, "\"userid=", "\"");
        }
        return userid;
    }
    
}
