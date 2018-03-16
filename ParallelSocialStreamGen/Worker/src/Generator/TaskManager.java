package Generator;

import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Generator.Util.ComparatorWorkerTask;
import Object.WorkerTask;

public class TaskManager {
	private final ReentrantLock lock = new ReentrantLock();
	private final Condition con_notEnoughItemsForTask = lock.newCondition();
	private Long waitTaskTime;
	private TreeSet<WorkerTask> tasks = new TreeSet<WorkerTask>(new ComparatorWorkerTask());
	private final Condition con_notHaveTask = lock.newCondition();

	public void trySingalLockOfNotTask() {
		lock.lock();
		try {
			if (lock.hasWaiters(con_notHaveTask)) {
				con_notHaveTask.signal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void addTask(WorkerTask task) {
		lock.lock();
		try {
			tasks.add(task);
			if (lock.hasWaiters(con_notHaveTask)) {
				con_notHaveTask.signal();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public WorkerTask getFirstTask() {
		lock.lock();
		WorkerTask task = null;
		try {
			if (tasks.isEmpty() && !Parameter.isSendTaskEnd) {
//				System.out.println("await con_notHaveTask");
				con_notHaveTask.await();
//				System.out.println("singal con_notHaveTask");
			}
			
			task = tasks.pollFirst();
			if(task!=null){
				boolean itemsGenerateEnd = Parameter.pool.getNextPool().isEmpty()
						&& Parameter.pool.getRecentPool().isEmpty();
				boolean notHaveEngoughItemsForTask = Parameter.pool.getFirstTimeFromNextPool() != null
						&& task.getTime() > Parameter.pool.getFirstTimeFromNextPool();
				if (!itemsGenerateEnd && notHaveEngoughItemsForTask) {
//					System.out.println("await con_notEnoughItemsForTask");
					waitTaskTime = task.getTime();
					con_notEnoughItemsForTask.await();
//					System.out.println("single con_notEnoughItemsForTask");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return task;
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