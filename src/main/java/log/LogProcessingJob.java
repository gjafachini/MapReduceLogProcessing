package log;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import api.Job;
import api.JobExecutionException;
import api.MRResult;

import com.google.common.collect.Lists;

public class LogProcessingJob implements Job {
    
    private final Collection<String> logFiles;
    private final LogParser parser;
    
    public LogProcessingJob(LogParser parser, String... logFiles) {
        this.parser = parser;
        this.logFiles = Lists.newArrayList(logFiles);
    }
    
    @Override
    public MRResult<String> map(String line) {
        return new MRResult<String>(this.parser.parseUserId(line), line);
    }
    
    @Override
    public Collection<String> getInputs() {
        return this.logFiles;
    }
    
    @Override
    public MRResult<List<LogModel>> reduce(String key, Collection<MRResult<?>> allKeyValues) {
        List<LogModel> userlogs = Lists.newArrayList();
        
        for (MRResult<?> mappingResult : allKeyValues) {
            LogModel model;
            try {
                model = this.parser.parse(mappingResult.getKey(), mappingResult.getValue());
            } catch (ParseException e) {
                throw new JobExecutionException("Error parsing log.", e);
            }
            userlogs.add(model);
        }
        Collections.sort(userlogs);
        return new MRResult<List<LogModel>>(key, userlogs);
    }
}
