/**   
* @Title: SendTaskResult.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年6月16日 
* @version V1.0   
*/
package Generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import Common.generator.DiscreteGenerator;
import Object.Item;
import Object.WorkerTask;

/** 
* @author Chengcheng Yu
* @version 2016年6月16日 下午7:14:46
*/
/**
 * @ClassName: SendTaskResult
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class SendTask implements Runnable {
	// private final Logger logger = Logger.getLogger("SendTask");
	CountDownLatch threadSignal;

	public SendTask(CountDownLatch threadSignal) {
		this.threadSignal = threadSignal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				Item task = Parameter.tasks.getFirstTask();
				if (task != null) {
					allocateTask(task);
				} else {
					if (Parameter.generateItemEndWorkers.size() == Parameter.workerNum) {
						for (int cl = 0; cl < Parameter.workerNum; cl++) {
							Parameter.workerIO.get(cl).writeSendTaskEnd();
						}
					}
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threadSignal.countDown();
	}

	private void allocateTask(Item item) throws IOException {
		// TODO Auto-generated method stub

		int linkNum = item.getLinkNum();

		Set<Integer> workers = Parameter.followInfo.get(item.getUid()).keySet();

		DiscreteGenerator<Integer> workerIDGen = new DiscreteGenerator<Integer>();
		for (Integer wID : workers) {
			if (!wID.equals(item.getWorkerID())) {
				double pro = Parameter.followInfo.get(item.getUid()).get(wID);
				workerIDGen.addValue(pro, wID);
			}
		}

		Map<Integer, Integer> workerID_taskNum = new HashMap<Integer, Integer>();
		for (int i = 0; i < linkNum; i++) {
			Integer workerID = workerIDGen.nextValue();
			if (workerID_taskNum.containsKey(workerID)) {
				workerID_taskNum.put(workerID, workerID_taskNum.get(workerID) + 1);
			} else {
				workerID_taskNum.put(workerID, 1);
			}
		}

		Parameter.taskAllocate.put(item.getId(), workerID_taskNum.keySet());
		Parameter.taskResults.trySingleNotAllocate(item.getId());
		// Parameter.taskAllocate.put(item.getId(), workerID_taskNum.size());
		// System.out.println("taskAllocate.put "+item.getId());
		for (Integer workerID : workerID_taskNum.keySet()) {
			WorkerTask wTask = new WorkerTask(item.getId(), item.getTime(), item.getUid(),
					workerID_taskNum.get(workerID));
			try {
				if (!Parameter.workerIO.containsKey(workerID)) {
					System.out.println("Parameter.workerIO do not have " + workerID);
				}

				// logger.info("begin send task to " + workerID);
				Parameter.workerIO.get(workerID).writeWorkerTask(wTask);
				// logger.info("end send task to " + workerID);
				Parameter.addOneCommunication();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
