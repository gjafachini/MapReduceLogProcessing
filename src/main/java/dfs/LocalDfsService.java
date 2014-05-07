package dfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDfsService implements DfsService {
    
    private static final String DFS_DIR = "dfs/";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDfsService.class);
    
    @Override
    public void save(String fileName, String content) throws DfsException {
        LOGGER.debug("Saving file {}", fileName);
        File localFile = new File(DFS_DIR + fileName);
        
        try {
            Files.write(localFile.toPath(), content.getBytes());
        } catch (IOException e) {
            throw new DfsException("Error saving file", e);
        }
    }
    
    @Override
    public File load(String fileName) throws DfsException {
        LOGGER.debug("Loading file {}", fileName);
        File file = new File(DFS_DIR + fileName);
        
        if (!file.exists()) { throw new DfsException("File not found"); }
        
        return file;
    }
    
    @Override
    public String mergeFiles(Collection<String> files) throws DfsException {
        String newFileName = UUID.randomUUID().toString();
        File mergedFile = new File(DFS_DIR + newFileName);
        try {
            mergedFile.createNewFile();
        } catch (IOException e) {
            throw new DfsException("Error creating merge file", e);
        }
        
        for (String fileName : files) {
            File inputFile = load(fileName);
            String line;
            boolean first = true;
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                    BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFile));) {
                while ((line = reader.readLine()) != null) {
                    if (!first) {
                        writer.newLine();
                    } else {
                        first = false;
                    }
                    writer.append(line);
                    // Files.write(mergedFile.toPath(), content.getBytes(),
                    // StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                throw new DfsException("Error merging file", e);
            }
        }
        return newFileName;
    }
    
    @Override
    public void moveFileTo(File file, String newFolder) {
        File createdFolder = new File(DFS_DIR + newFolder);
        if (!createdFolder.exists()) {
            createdFolder.mkdir();
        }
        File newFilePlace = new File(createdFolder.getPath() + "/" + file.getName());
        file.renameTo(newFilePlace);
    }
    
}
