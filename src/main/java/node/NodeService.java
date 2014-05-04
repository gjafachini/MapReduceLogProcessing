package node;

import java.util.Map;
import java.util.concurrent.FutureTask;

import master.Job;

public interface NodeService {

    FutureTask<String> map(Job job, String input);

    Map<String, String> shuffle(String splittingFileName) throws NodeServiceException;

    FutureTask<String> reduce(Job job, String mergedFileName);

}
