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

    private static final String DDFS_DIR = "ddfs/";
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDfsService.class);

    @Override
    public void save(String fileName, String content) throws DfsException {
        LOGGER.debug("Saving file {0}", fileName);
        File localFile = new File(DDFS_DIR + fileName);

        try {
            Files.write(localFile.toPath(), content.getBytes());
        } catch (IOException e) {
            throw new DfsException("Error saving file", e);
        }
    }

    @Override
    public File load(String fileName) throws DfsException {
        LOGGER.debug("Loading file {0}", fileName);
        File file = new File(DDFS_DIR + fileName);

        if (!file.exists()) {
            throw new DfsException("File not found");
        }

        return file;
    }

    @Override
    public String mergeFiles(Collection<String> files) throws DfsException {
        String newFileName = UUID.randomUUID().toString();
        File mergedFile = new File(DDFS_DIR + newFileName);

        for (String fileName : files) {
            try (FileInputStream in = new FileInputStream(DDFS_DIR + fileName)) {
                byte[] chunk = new byte[1024];
                int chunkLength = 0;

                while ((chunkLength = in.read(chunk)) != -1) {
                    Files.write(mergedFile.toPath(), chunk, StandardOpenOption.APPEND);
                }

            } catch (Exception e) {
                throw new DfsException("Error merging file", e);
            }
        }

        return newFileName;
    }
}
