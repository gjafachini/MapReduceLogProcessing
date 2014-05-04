package log;

import java.util.List;

import master.Job;
import master.MappingResult;

public class LogProcessingJob implements Job {

    @Override
    public MappingResult<?> map(String line) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getInputs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void reduce(String key, List<String> values) {
        // TODO Auto-generated method stub

    }

}
