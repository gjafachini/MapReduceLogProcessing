package log;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;

import master.Job;
import master.JobExecutionException;
import master.MappingResult;

import org.apache.commons.lang3.StringUtils;

import com.btaz.util.files.SortController;
import com.google.common.collect.Lists;

import dfs.DfsException;
import dfs.DfsService;

public class LogProcessingJob implements Job {

    private final static String USER_ID_PATTERN = "userid=";
    private DfsService dfs;
    private Collection<String> logFiles;

    public LogProcessingJob(DfsService dfs, String... logFiles) {
        this.logFiles = Lists.newArrayList(logFiles);
        this.dfs = dfs;
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
    public String reduce(String key, File reducedFile) throws JobExecutionException {
        File outputFile;
        try {
            outputFile = dfs.createFile(key);
        } catch (DfsException e) {
            throw new JobExecutionException("Error during sorting process", e);
        }

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                LogModel log1 = null;
                LogModel log2 = null;

                try {
                    log1 = LogParser.parseLog(o1);
                    log2 = LogParser.parseLog(o2);
                } catch (JobExecutionException e) {
                    e.printStackTrace();
                }

                return log1.compareTo(log2);
            }
        };

        SortController.sortFile(dfs.getTempDir(), reducedFile, outputFile, comparator, false);

        return null;
    }
}
