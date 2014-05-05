package log;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

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
    public String reduce(String key, Collection<?> values) throws JobExecutionException {
        Iterator<?> valuesIterator = values.iterator();
        Collection<String> shuffledFilenames = Lists.newArrayList();
        String mergedFileName;
        File inputFile;
        File outputFile;
        Comparator<String> comparator = getLogComparator();

        while (valuesIterator.hasNext()) {
            shuffledFilenames.add((String) valuesIterator.next());
        }

        try {
            mergedFileName = dfs.mergeFiles(shuffledFilenames);
            outputFile = dfs.createFile(key);
            inputFile = dfs.load(mergedFileName);
        } catch (DfsException e) {
            throw new JobExecutionException("Error cduring sorting process", e);
        }

        SortController.sortFile(dfs.getTempDir(), inputFile, outputFile, comparator, false);

        return outputFile.getName();
    }

    private Comparator<String> getLogComparator() {
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
        return comparator;
    }
}
