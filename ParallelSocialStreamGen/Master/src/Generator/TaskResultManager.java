/**   
* @Title: TaskResultManager.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年6月12日 
* @version V1.0   
*/
package Generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Object.Item;
import Object.TaskResult;

/** 
* @author Chengcheng Yu
* @version 2016年6月12日 下午5:18:18
*/
/**
 * @ClassName: TaskResultManager
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class TaskResultManager {
//	private final Logger logger = Logger.getLogger("TaskResultManager");
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition con_notHaveResult = lock.newCondition();
	private final Condition con_notAllocate = lock.newCondition();
	private Map<String, Set<TaskResult>> taskResults = new HashMap<String, Set<TaskResult>>();
	private String waitTaskResult;
	private String waitAllocate;

	public int getResultSize() {
		//lock.lock();
		int result = 0;
		try {
			result = taskResults.size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//lock.unlock();
		}
		return result;
	}

	public Set<String> getResult(Item item) {
//		logger.info("getResult for " + item.getWorkerID() + "; get lock");
		lock.lock();
//		logger.info("getResult for " + item.getWorkerID() + "; lock is getted");
		Set<String> links = new HashSet<String>();
		try {
			String id = item.getId();
			if (!Parameter.taskAllocate.containsKey(id)) {
				waitAllocate = id;
//				Parameter.outToLogFile(" con_notAllocate is locked");
//				logger.info(" con_notAllocate is locked");
				con_notAllocate.await();
//				Parameter.outToLogFile(" con_notAllocate is awaked");
//				logger.info(" con_notAllocate is awaked");
			}

			if (!taskResults.containsKey(id) || (Parameter.taskAllocate.containsKey(id)
					&& taskResults.get(id).size() < Parameter.taskAllocate.get(id).size())) {
				Set<Integer> waitWorkers = Parameter.taskAllocate.get(id);

				Parameter.waitResultWorker.addAll(waitWorkers);
//				logger.info("iterate through " + waitWorkers.size());
				for (Integer workerID : waitWorkers) {
//					System.out.println("getResult() from " + workerID);
					if (Parameter.items.isWorkerAwait(workerID)) {
//						System.out.println(" getResult() trySignalSuspendReceive for " + workerID);
						Parameter.items.trySignalSuspendReceive(workerID);
//						System.out.println(" getResult()  end trySignalSuspendReceive for " + workerID);
					}
				}

//				String message1 = "getResult() is locked " ;
//				System.out.println(message1);
				waitTaskResult = id;
				con_notHaveResult.await();
				
				
//				String message2 = "getResult() is awaked ";
//				System.out.println(message2);

				Parameter.waitResultWorker.removeAll(waitWorkers);
				// System.out.println("singal con_notHaveResult"+ id);
			}

			for (TaskResult result : taskResults.get(id)) {
				links.addAll(result.getLinks());
			}

			Parameter.taskAllocate.remove(id);
			taskResults.remove(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return links;
	}

	public void addResult(TaskResult result) {
		lock.lock();
		try {
			if (taskResults.containsKey(result.getId())) {
				taskResults.get(result.getId()).add(result);
			} else {
				Set<TaskResult> results = new HashSet<TaskResult>();
				results.add(result);
				taskResults.put(result.getId(), results);
			}

			if (lock.hasWaiters(con_notHaveResult) && waitTaskResult != null && waitTaskResult.equals(result.getId())) {

				if (Parameter.taskAllocate.get(result.getId()).size() == taskResults.get(result.getId()).size()) {
					con_notHaveResult.signal();
					waitTaskResult = null;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void trySingleNotAllocate(String id) {
		lock.lock();
		try {
			if (waitAllocate != null && waitAllocate.equals(id) && lock.hasWaiters(con_notAllocate)) {
				con_notAllocate.signal();
				waitAllocate = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
