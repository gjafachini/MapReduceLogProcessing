package node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import master.Job;
import master.MappingResult;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

import dfs.DfsException;
import dfs.DfsService;

public class ThreadedNodeService implements NodeService {

    protected static final Object NEW_LINE = "\n";
    private DfsService dfs;
    private ExecutorService executor;
    private Gson gson;
    private boolean isIdle;

    public ThreadedNodeService(DfsService dfs, ExecutorService executor) {
        this.dfs = dfs;
        this.executor = executor;
        this.gson = new Gson();
        setIdle(true);
    }

    @Override
    public FutureTask<String> map(final Job job, final String input) {
        setIdle(false);
        FutureTask<String> mapTask = new FutureTask<>(new Callable<String>() {

            @Override
            public String call() throws Exception {
                File mapFile = dfs.load(input);
                String newFileName = UUID.randomUUID().toString();

                try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
                    String line;
                    StringBuilder mappedLines = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        MappingResult<?> result = job.map(line);
                        String json = gson.toJson(result);

                        mappedLines.append(json).append(NEW_LINE);
                    }
                    dfs.saveTempFile(newFileName, mappedLines.toString());
                }

                setIdle(true);
                return newFileName;
            }
        });

        executor.execute(mapTask);
        return mapTask;
    }

    @Override
    public Map<String, String> shuffle(String splittingFileName) throws NodeServiceException {
        setIdle(false);
        Map<String, String> shuffledFilesData = new HashMap<String, String>();
        File mappedFile;

        try {
            mappedFile = dfs.loadTempFile(splittingFileName);
        } catch (DfsException e) {
            throw new NodeServiceException("Error loading mapped file on shuffling", e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(mappedFile))) {
            String line;
            Multimap<String, String> shuffledData = ArrayListMultimap.create();

            while ((line = reader.readLine()) != null) {
                MappingResult<?> mapedData = gson.fromJson(line, MappingResult.class);

                shuffledData.put(mapedData.getKey(), line);
            }

            for (String key : shuffledData.keySet()) {
                StringBuilder fileContent = new StringBuilder();
                Collection<String> oneKeyData = shuffledData.get(key);
                String filename = key + "_" + UUID.randomUUID();

                for (String dataLine : oneKeyData) {
                    fileContent.append(dataLine).append(NEW_LINE);
                }

                try {
                    dfs.saveTempFile(filename, fileContent.toString());
                    shuffledFilesData.put(key, filename);
                } catch (DfsException e) {
                    throw new NodeServiceException("Error saving shuffled data", e);
                }
            }

        } catch (IOException e) {
            throw new NodeServiceException("Error reading mapped file on shuffling", e);
        }
        setIdle(true);
        return shuffledFilesData;
    }

    @Override
    public FutureTask<String> reduce(final Job job, final String key, final Collection<?> shuffledList) {
        setIdle(false);
        FutureTask<String> reduceTask = new FutureTask<>(new Callable<String>() {

            @Override
            public String call() throws Exception {

                String reducedFileName;

                // List of values changed for a File reference.
                reducedFileName = job.reduce(key, shuffledList);

                setIdle(true);
                // Restricting reduce process to return only one reduced
                // reference.
                return reducedFileName;
            }
        });

        executor.execute(reduceTask);
        return reduceTask;
    }

    @Override
    public synchronized boolean isIdle() {
        return isIdle;
    }

    private synchronized void setIdle(boolean isIdle) {
        this.isIdle = isIdle;
    }
}
