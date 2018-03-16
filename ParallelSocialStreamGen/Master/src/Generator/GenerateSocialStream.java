package Generator;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import Object.ComparatorItem;
import Object.Item;

public class GenerateSocialStream implements Runnable {
//	private final Logger logger = Logger.getLogger("ItemManager");
	private CountDownLatch threadsSignal;

	public GenerateSocialStream(CountDownLatch threadsSignal) {
		this.threadsSignal = threadsSignal;
	}

	public void run() {
		System.out.println("Master GenerateSocialStream start.");
		Thread.currentThread().setName("GenerateSocialStream");
		try {
			TreeSet<Item> nextPool = new TreeSet<Item>(new ComparatorItem());
			for (int workerID = 0; workerID < Parameter.workerNum; workerID++) {
//				logger.info("begin getfirstitem");
				Item first = Parameter.items.getFirstItem(workerID);
//				logger.info("end getfirstitem");
				nextPool.add(first);
			}
			while (!nextPool.isEmpty()) {
				Item first = nextPool.pollFirst();
				if (first.getLinkNum() > 0) {
//					logger.info("begin getResult");
					Set<String> links = Parameter.taskResults.getResult(first);
//					logger.info("begin getResult");
					first.getLinks().addAll(links);
				}
				
				if (first.getLinks() != null) {
					Parameter.addLinkNum(first.getLinks().size());
				}
				Parameter.outToFile(first);
//				Parameter.outStreamToFile(first);
				Parameter.addOneItem();
				viewOutput(first);
//				logger.info("begin getFirstItem");
				Item next = Parameter.items.getFirstItem(first.getWorkerID());
//				logger.info("begin getFirstItem");
				if (next != null) {
					nextPool.add(next);
				}
			}
			Parameter.closeFile();
			threadsSignal.countDown();
			System.out.println("Master GenerateSocialStream end.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private int identifier=0;
	private void viewOutput(Item item) {
		// TODO Auto-generated method stub
		Date d = new Date(item.getTime()*1000);
		int it=0;
		if(Parameter.viewSize.equals("day")){
			it = d.getDay();
		}else if(Parameter.viewSize.equals("month")){
			it = d.getMonth();
		}else if(Parameter.viewSize.equals("year")){
			it = d.getYear();
		}
		
		if(identifier != it){
			identifier= it;
			System.out.println(item.toOut());
		}
		Parameter.setUsedMemory(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
	}
}