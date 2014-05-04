package dfs;

public class DfsException extends Exception {

    public DfsException(String message, Throwable e) {
        super(message, e);
    }

    public DfsException(String message) {
        super(message);
    }

}
