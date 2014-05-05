package log;

import java.io.BufferedReader;
import java.util.Collection;

import master.Job;
import master.MappingResult;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class LogProcessingJob implements Job {

    private final static String USER_ID_PATTERN = "userid=";
    private Collection<String> logFiles;

    public LogProcessingJob(String... logFiles) {
        this.logFiles = Lists.newArrayList(logFiles);
    }

    @Override
    public MappingResult<?> map(String line) {
        String userID = "";

        if (line.contains(USER_ID_PATTERN)) {
            userID = StringUtils.substringBetween(line, "\"userid=", "\"");
        }

        return new MappingResult<String>(userID, line);
    }

    @Override
    public Collection<String> getInputs() {
        return logFiles;
    }

    @Override
    public String reduce(String key, BufferedReader reader) {
        String line;
        Multimap<String, String> shuffledData = ArrayListMultimap.create();

        while ((line = reader.readLine()) != null) {

        }

        return null;
    }

}
