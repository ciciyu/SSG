package Generator;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Object.TaskResult;
import Object.WorkerTask;

public class ReceiveTask implements Runnable {
	CountDownLatch threadSignal;

	public ReceiveTask( CountDownLatch threadSignal) {
		this.threadSignal = threadSignal;
	}

	public void run() {
		Thread.currentThread().setName("receiveMessage");
		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " ReceiveTask start.");
		// TODO Auto-generated method stub
		while (true) {
			// System.out.println("receive message running...");
			String Handler = "";
			try {
				Handler = Parameter.io.readHandler();
				if (Handler != null) {
					if (Handler.equals("WorkerTask")) {
						WorkerTask task = Parameter.io.readWorkerTask();
						Parameter.tasks.addTask(task);
//						if(Parameter.tasks.isHaveEnoughItemsForTask(task)){
//							TaskResult result = Parameter.genTool.getTaskLink(task);
//							Parameter.io.writeTaskResult(result);
//						}
						
					} else if (Handler.equals("SendTaskEnd")) {
						Parameter.isSendTaskEnd=true;
						Parameter.tasks.trySingalLockOfNotTask();
						break;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		threadSignal.countDown();

		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " ReceiveTask end.");

	}
}