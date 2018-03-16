/**   
* @Title: ReceiveMessage.java 
* @Package Generator 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年5月18日 
* @version V1.0   
*/
package Generator;

import java.util.concurrent.CountDownLatch;

import Object.Item;
import Object.TaskResult;

/** 
* @author Chengcheng Yu
* @version 2016年5月18日 下午2:40:58
*/
/**
 * @ClassName: ReceiveMessage
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class ReceiveMessage implements Runnable {
	Integer workerID;
	CountDownLatch threadSignal;

	public ReceiveMessage(Integer workerID, CountDownLatch threadSignal) {
		this.workerID = workerID;
		this.threadSignal = threadSignal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		// TODO Auto-generated method stub
		System.out.println("Master receive message start.");
		while (true) {
			try {
				IO io = Parameter.workerIO.get(workerID);
				String Handler = io.readHandler();
				// System.out.println("Master receive message....");
				if (Handler != null) {
					// System.out.println("handler="+Handler);
					if (Handler.equals("ItemCom")) {
						Item item = io.readItemCom();
						item.setWorkerID(workerID);
						if (item.getLinkNum() > 0) {
							Parameter.tasks.addTask(item);
						}
						Parameter.items.addItem(item);
						Parameter.addOneCommunication();
					} else if (Handler.equals("TaskResult")) {
						TaskResult result = io.readTaskResult();
//						 Parameter.outLinkToFile(result);
						Parameter.taskResults.addResult(result);
						
						Parameter.addOneCommunication();
					} else if (Handler.equals("SendItemsEnd")) {
						System.out.println("Worker " + workerID + " SendItemsEnd");
						Parameter.generateItemEndWorkers.add(workerID);
						Parameter.items.trySignal(workerID);
						Parameter.tasks.trySignalTask();
						
					} else if (Handler.equals("SendTaskResultEnd")) {

						Parameter.sendTaskResultEndWorkers.add(workerID);
					}

					if (Parameter.generateItemEndWorkers.contains(workerID)
							&& Parameter.sendTaskResultEndWorkers.contains(workerID)) {
						break;
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		threadSignal.countDown();
		System.out.println("Master receive message from workerID " + workerID + " end.");
	}

	
}
