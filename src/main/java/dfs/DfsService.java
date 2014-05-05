package dfs;

import java.io.File;
import java.util.Collection;

public interface DfsService {

    File load(String fileName) throws DfsException;

    String mergeFiles(Collection<String> collection) throws DfsException;

    void save(String fileName, String content) throws DfsException;

    File getTempDir();

    File createFile(String fileName) throws DfsException;
}
