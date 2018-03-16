/**   
* @Title: TaskResultManager.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年6月12日 
* @version V1.0   
*/
package Generator;

import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Object.ComparatorItem;
import Object.Item;

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
public class TaskManager {
//	private final Logger logger = Logger.getLogger("TaskManager");
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition con_notHaveTask = lock.newCondition();
	private TreeSet<Item> task = new TreeSet<Item>(new ComparatorItem());

	public int getTaskSize(){
		int result=0;
		result=task.size();
		return result;
	}
	
	public void addTask(Item item) {
		lock.lock();
		try {
			task.add(item);
			if (lock.hasWaiters(con_notHaveTask)) {
				con_notHaveTask.signal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public Item getFirstTask() {
		lock.lock();
		Item result = null;
		try {
			if (task.isEmpty() && Parameter.generateItemEndWorkers.size() != Parameter.workerNum) {
//System.out.println("con_notHaveTask is locked");
				con_notHaveTask.await();
//				Parameter.outToLogFile("con_notHaveTask is awaked");
//System.out.println("con_notHaveTask is awaked");
			}
			if (!task.isEmpty()) {
				result = task.pollFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return result;
	}

	public void trySignalTask() {
		lock.lock();
		try {
			if (lock.hasWaiters(con_notHaveTask) && 
					Parameter.generateItemEndWorkers.size() == Parameter.workerNum) {
				con_notHaveTask.signal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
