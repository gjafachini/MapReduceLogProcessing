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
     * 
     * @param fileName
     * @return loaded file
     * @throws DfsException
     */
    File load(String fileName) throws DfsException;

    /**
     * Merges a collection of file into one.
     * 
     * @param collection
     *            file names to merge
     * @return merged file name
     * @throws DfsException
     */
    String mergeFiles(Collection<String> collection) throws DfsException;

    /**
     * Save file to DFS.
     * 
     * @param fileName
     *            to save.
     * @param content
     *            to save in the file
     * @throws DfsException
     */
    void save(String fileName, String content) throws DfsException;

    /**
     * Create file in DFS.
     * 
     * @param fileName
     * @return created file.
     * @throws DfsException
     */
    File createFile(String fileName) throws DfsException;

    /**
     * Move a file to another logical folder.
     * 
     * @param string
     */
    void moveFileTo(File file, String newFolder);

}
