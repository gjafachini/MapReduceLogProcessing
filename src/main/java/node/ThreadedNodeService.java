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

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import dfs.DfsException;
import dfs.DfsService;

public class ThreadedNodeService implements NodeService {

    private static final String JSON_END_FILE = ".json";
    protected static final Object NEW_LINE = "\n";
    private DfsService dfs;
    private ExecutorService executor;
    private Gson gson;

    public ThreadedNodeService(DfsService dfs, ExecutorService executor, Gson gson) {
        this.dfs = dfs;
        this.executor = executor;
        this.gson = gson;
    }

    @Override
    public FutureTask<String> map(final Job job, final String input) {
        FutureTask<String> mapTask = new FutureTask<>(new Callable<String>() {

            @Override
            public String call() throws Exception {
                File mapFile = dfs.load(input);
                String newFileName = UUID.randomUUID().toString() + JSON_END_FILE;

                try (BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
                    String line;
                    StringBuilder mappedLines = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        MappingResult<?> result = job.map(line);
                        String json = gson.toJson(result);

                        mappedLines.append(json).append(NEW_LINE);
                    }
                    dfs.save(newFileName, mappedLines.toString());
                }

                return newFileName;
            }
        });

        executor.execute(mapTask);

        return mapTask;
    }

    @Override
    public Map<String, String> shuffle(String splittingFileName) throws NodeServiceException {
        Map<String, String> shuffledFilesData = new HashMap<String, String>();
        File mappedFile;

        try {
            mappedFile = dfs.load(splittingFileName);
        } catch (DfsException e) {
            throw new NodeServiceException("Error loading mapped file on shuffling", e);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(mappedFile))) {
            String line;
            Map<String, Collection<String>> shuffledData = new HashMap<String, Collection<String>>();

            while ((line = reader.readLine()) != null) {
                MappingResult<?> mapedData = gson.fromJson(line, MappingResult.class);

                if (shuffledData.get(mapedData.getKey()) != null) {
                    Collection<String> shuffledList = Lists.newArrayList();
                    shuffledData.put(mapedData.getKey(), shuffledList);
                }

                shuffledData.get(mapedData.getKey()).add(line);
            }

            for (String key : shuffledData.keySet()) {
                StringBuilder fileContent = new StringBuilder();
                Collection<String> oneKeyData = shuffledData.get(key);
                String filename = key + "_" + UUID.randomUUID() + JSON_END_FILE;

                for (String dataLine : oneKeyData) {
                    fileContent.append(dataLine).append(NEW_LINE);
                }

                try {
                    dfs.save(filename, fileContent.toString());
                    shuffledFilesData.put(key, filename);
                } catch (DfsException e) {
                    throw new NodeServiceException("Error saving shuffled data", e);
                }
            }

        } catch (IOException e) {
            throw new NodeServiceException("Error reading mapped file on shuffling", e);
        }

        return shuffledFilesData;
    }

    @Override
    public FutureTask<String> reduce(Job job, String mergedFileName) {
        // TODO Auto-generated method stub
        return null;
    }

}
