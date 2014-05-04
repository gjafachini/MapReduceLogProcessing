package master;

public class JobExecutionException extends Exception {

    public JobExecutionException(String message, Exception e) {
        super(message, e);
    }

}
