package node;

public class NodeServiceException extends Exception {
    private static final long serialVersionUID = 6394009362650862105L;

    public NodeServiceException(String message, Throwable e) {
        super(message, e);
    }

}
