package dfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDfsService implements DfsService {

    private static final String DFS_DIR = "dfs/";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDfsService.class);

    @Override
    public void save(String fileName, String content) throws DfsException {
        LOGGER.debug("Saving file {0}", fileName);
        File localFile = new File(DFS_DIR + fileName);

        try {
            Files.write(localFile.toPath(), content.getBytes());
        } catch (IOException e) {
            throw new DfsException("Error saving file", e);
        }
    }

    @Override
    public File load(String fileName) throws DfsException {
        LOGGER.debug("Loading file {0}", fileName);
        File file = new File(DFS_DIR + fileName);

        if (!file.exists()) {
            throw new DfsException("File not found");
        }

        return file;
    }

    @SuppressWarnings("unused")
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
            byte[] chunk = new byte[1024];
            int chunkLength = 0;

            if (inputFile.length() < 1024) {
                int fileLength = Long.valueOf(inputFile.length()).intValue();
                chunk = new byte[fileLength];
            }

            try (FileInputStream in = new FileInputStream(DFS_DIR + fileName)) {
                while ((chunkLength = in.read(chunk)) != -1) {
                    Files.write(mergedFile.toPath(), chunk, StandardOpenOption.APPEND);
                }

            } catch (Exception e) {
                throw new DfsException("Error merging file", e);
            }
        }

        return newFileName;
    }

    @Override
    public File createFile(String fileName) throws DfsException {
        File newFile = new File(DFS_DIR + fileName);
        if (newFile.exists()) {
            throw new DfsException("This file already exists.");
        }
        return newFile;
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
