package node;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.FutureTask;

import master.Job;

public interface NodeService {

    FutureTask<String> map(Job job, String input);

    Map<String, String> shuffle(String splittingFileName) throws NodeServiceException;

    boolean isIdle();

    FutureTask<String> reduce(Job job, String key, Collection<?> collection);

}
