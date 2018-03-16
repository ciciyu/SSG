package Generator;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Object.WorkerTask;

public class TaskManager1 {
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition con_notEnoughItemsForTask = lock.newCondition();
	private Long waitTaskTime;
//	private TreeSet<WorkerTask> tasks = new TreeSet<WorkerTask>(new ComparatorWorkerTask());
//	private final Condition con_notHaveTask = lock.newCondition();

	

	public boolean isHaveEnoughItemsForTask(WorkerTask task) {
		boolean reuslt = false;
		lock.lock();
		try {
			boolean itemsGenerateEnd = Parameter.pool.getNextPool().isEmpty()
					&& Parameter.pool.getRecentPool().isEmpty();
			boolean notHaveEngoughItemsForTask = Parameter.pool.getFirstTimeFromNextPool() != null
					&& task.getTime() > Parameter.pool.getFirstTimeFromNextPool();
			if (!itemsGenerateEnd && notHaveEngoughItemsForTask) {
//				System.out.println("await con_notEnoughItemsForTask");
				waitTaskTime = task.getTime();
				con_notEnoughItemsForTask.await();
//				System.out.println("single con_notEnoughItemsForTask");
			}
			reuslt = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return reuslt;
	}
	
	public void trySingalLockOfNotEnoughItemsFortask() {
		lock.lock();
		try {
			if (lock.hasWaiters(con_notEnoughItemsForTask) && (Parameter.pool.getNextPool().isEmpty()
					|| (waitTaskTime != null && waitTaskTime <= Parameter.pool.getFirstTimeFromNextPool()))) {
				con_notEnoughItemsForTask.signal();
				waitTaskTime = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	

}