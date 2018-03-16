/**   
* @Title: ItemsManager.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年6月12日 
* @version V1.0   
*/
package Generator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Object.Item;

/** 
* @author Chengcheng Yu
* @version 2016年6月12日 下午5:19:17
*/
/**
 * @ClassName: ItemsManager
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class ItemManager {
//	private final Logger logger = sLogger.getLogger("ItemManager");
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition con_notHaveItems = lock.newCondition();
	private final Map<Integer, Condition> con_suspendReceive = new HashMap<Integer, Condition>();// lock.newCondition();
	private HashMap<Integer, LinkedList<Item>> items = new HashMap<Integer, LinkedList<Item>>();
	// <slaveID,items>
	private Integer waitItemFromWorker;

	public ItemManager() {
		for (int i = 0; i < Parameter.workerNum; i++) {
			items.put(i, new LinkedList<Item>());
			con_suspendReceive.put(i, lock.newCondition());
		}
	}

//	public Integer getWorkerItemSize(Integer workerID) {
//		lock.lock();
//		int result = 0;
//		try {
//			result = items.get(workerID).size();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			lock.unlock();
//		}
//		return result;
//	}

	public void addItem(Item item) {
		lock.lock();
		try {
			// add item to nextPool
			if (items.get(item.getWorkerID()).size() > Parameter.itemsSizeInWorker
					&& (waitItemFromWorker == null
							|| (waitItemFromWorker != null && !waitItemFromWorker.equals(item.getWorkerID())))
					&& !Parameter.waitResultWorker.contains(item.getWorkerID())) {
//				String message1 ="Woreker "+ item.getWorkerID()+" receiving is blocked. size: "
//						+ items.get(item.getWorkerID()).size()+" task size:" +Parameter.tasks.getTaskSize()
//						+" taskResult size: "+Parameter.taskResults.getResultSize();
//				System.out.println(message1);
//				con_suspendReceive.get(item.getWorkerID()).await(5, TimeUnit.SECONDS);
				
				con_suspendReceive.get(item.getWorkerID()).await();
				
				
//				String message2 ="Woreker "+ item.getWorkerID()+" receiving is awake.  "  + " size: "
//						+ items.get(item.getWorkerID()).size()+" task size:" +Parameter.tasks.getTaskSize()
//						+" taskResult size: "+Parameter.taskResults.getResultSize();
//				System.out.println(message2);
			}

			items.get(item.getWorkerID()).add(item);

			if (lock.hasWaiters(con_notHaveItems) && waitItemFromWorker != null
					&& waitItemFromWorker.equals(item.getWorkerID())) {
				con_notHaveItems.signal();
				waitItemFromWorker = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public Item getFirstItem(Integer workerID) {
		lock.lock();
		Item result = null;
		try {

			if (items.get(workerID).isEmpty() && lock.hasWaiters(con_suspendReceive.get(workerID))) {
				con_suspendReceive.get(workerID).signal();
			}

			if (items.get(workerID).isEmpty() && !Parameter.generateItemEndWorkers.contains(workerID)) {
//				String message1="Worker "+workerID+" con_notHaveItems is locked. size: "+items.get(workerID).size();
//				System.out.println(message1);
				waitItemFromWorker = workerID;
				con_notHaveItems.await();
//				String message2 ="Worker "+workerID+" con_notHaveItems is awaked. size: "+items.get(workerID).size();
//				 System.out.println(message2);
			}

			if (!items.get(workerID).isEmpty()) {
				result = items.get(workerID).pollFirst();
			}

			if (lock.hasWaiters(con_suspendReceive.get(workerID))
					&& items.get(workerID).size() < Parameter.itemsSizeInWorker) {
				con_suspendReceive.get(workerID).signal();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return result;
	}

	public void trySignal(Integer workerID) {
		lock.lock();
		try {
			if (lock.hasWaiters(con_notHaveItems) && waitItemFromWorker != null
					&& waitItemFromWorker.equals(workerID)) {
				con_notHaveItems.signal();
				waitItemFromWorker = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public Boolean isWorkerAwait(Integer workerID) {
		Boolean result = null;
		lock.lock();
		try {
			result = lock.hasWaiters(con_suspendReceive.get(workerID));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return result;
	}

	public void trySignalSuspendReceive(Integer workerID) {
		lock.lock();
		try {
			if (lock.hasWaiters(con_suspendReceive.get(workerID))) {
				con_suspendReceive.get(workerID).signal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	

	
}
