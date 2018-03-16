package Generator;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import Object.Item;
import Object.ItemCom;

/**
 * @author Chengcheng Yu
 * 
 */
public class SSSGenerator implements Runnable {
	CountDownLatch threadSignal;

	public SSSGenerator(CountDownLatch threadSignal) throws Exception {
		this.threadSignal = threadSignal;
	}

	/**
	 * Generate social stream.
	 */
	public void run() {
		Thread.currentThread().setName("SSSGenerator");

		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " generate substream start.");
		try {
			initNextPool();
			updatePool();
			Parameter.io.writeSendItemsEnd();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("SSSGenerator error!");
			e.printStackTrace();
		}
		threadSignal.countDown();

		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " generate substream end.");
	}

	/**
	 * Get the first item of each producer for initialization nextPool.
	 */
	private void initNextPool() throws ParseException {

		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " initNextPool start.");
		long start = Parameter.workerInfo.getStartTime();
		for (String uid : Parameter.users.keySet()) {
			Long time = Parameter.genTool.nextTime(start, uid);
			if (time != null) {
				Parameter.pool.addItemToNextPool(new Item(time, uid));
			}
		}
		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " initNextPool end.");
	}

	/**
	 * Update these two pools (nextPool and recentPool) over time for generating
	 * social streams.
	 */
	private void updatePool() throws Exception {
		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " updatePool start.");

		while (!Parameter.pool.getNextPool().isEmpty()) {
			/* remove the first item in nextPool */
			Item item = Parameter.pool.removeFirstItemInNextPool();

			Parameter.tasks.trySingalLockOfNotEnoughItemsFortask();
			/* get the next item without link set, item.uid publish */
			Long nextTime = Parameter.genTool.nextTime(item);
			if (nextTime != null) {
				/* update the next item to nextPool */
				Parameter.pool.addItemToNextPool(new Item(nextTime, item.getUid()));
			}

			/* set the item's identifier */
			item.setId(String.valueOf(Parameter.workerInfo.getWorkerID()) + "w"
					+ String.valueOf(Parameter.IDGen.nextValue()));
			/* set the item's link set */

			viewOutput(item);
			ItemCom sendItem = Parameter.genTool.generateItemCom(item);

			Parameter.io.writeItemCom(sendItem);

			/* put the item to recent Pool */
			Parameter.pool.putToRecentPool(item);

		}

		Parameter.tasks.trySingalLockOfNotEnoughItemsFortask();
		System.out.println("Workder " + Parameter.workerInfo.getWorkerID() + " updatePool end.");
	}

	/**
	 * @Title: viewOutput @Description: TODO @param @return void @throws
	 */
	private int identifier = 0;

	private void viewOutput(Item item) {
		// TODO Auto-generated method stub
		Date d = new Date(item.getTime() * 1000);
		int it = 0;
		if (Parameter.viewSize.equals("day")) {
			it = d.getDay();
		} else if (Parameter.viewSize.equals("month")) {
			it = d.getMonth();
		} else if (Parameter.viewSize.equals("year")) {
			it = d.getYear();
		}

		if (identifier != it) {
			identifier = it;
			System.out.println(item.toString());
		}
		Parameter.setUsedMemory(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());

	}

	/**
	 * create out file
	 */

}