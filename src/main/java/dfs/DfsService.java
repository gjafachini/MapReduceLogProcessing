package dfs;

import java.io.File;
import java.util.Collection;

/**
 * DFS interface for Map-Reduce process system.
 *
 */
public interface DfsService {

    /**
     * Load files from DSF.
     * @param fileName
     * @return loaded file
     * @throws DfsException
     */
    File load(String fileName) throws DfsException;

    /**
     * Merges a collection of file into one.
     * @param collection file names to merge
     * @return merged file name
     * @throws DfsException
     */
    String mergeFiles(Collection<String> collection) throws DfsException;

    /**
     * Save file to DFS.
     * @param fileName to save.
     * @param content to save in the file
     * @throws DfsException
     */
    void save(String fileName, String content) throws DfsException;

    /**
     * @return temporary folder.
     */
    File getTempDir();

    /**
     * Create file in DFS.
     * @param fileName
     * @return created file.
     * @throws DfsException
     */
    File createFile(String fileName) throws DfsException;

    /**
     * Save temporary file in DFS.
     * @param newFileName
     * @param content
     * @throws DfsException
     */
    void saveTempFile(String newFileName, String content) throws DfsException;

    /**
     * Load temporary file from DFS.
     * @param fileName
     * @return loaded file
     * @throws DfsException
     */
    File loadTempFile(String fileName) throws DfsException;

    /**
     * Create temporary file in DFS.
     * @param fileName
     * @return created file
     * @throws DfsException
     */
    File createTempFile(String fileName) throws DfsException;
}
