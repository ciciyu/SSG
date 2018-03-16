package Generator;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Main {

	public static void main(String args[]) throws Exception {

		new Parameter(args[0],Integer.valueOf(args[1]));
		System.out.println("Master listen ...");
		@SuppressWarnings("resource")
		ServerSocket listen = new ServerSocket(Parameter.TESTPORT);
		while (true) {
			Socket socket = listen.accept();
			new Connects(socket);
			if (Parameter.getConnectNum() == Parameter.workerNum) {
				System.out.println("All workers connected finished!");
				break;
			}
		}
		CountDownLatch threadSignal = new CountDownLatch(Parameter.workerNum + 2);

		long start = System.currentTimeMillis();
		for(int workerID=0;workerID<Parameter.workerNum;workerID++){
			new Thread(new ReceiveMessage(workerID,  threadSignal)).start();
		}
		new Thread(new GenerateSocialStream(threadSignal)).start();
		new Thread(new SendTask(threadSignal)).start();
		
		try {
			threadSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server end!");
		double runningTime = (System.currentTimeMillis() - start) ;

		System.out.println("itemNum: " + Parameter.getItemNum());
		System.out.println("linkNum: " + Parameter.linkNum);
		System.out.println("running time: " + runningTime);
		System.out.println("Throught(item) : " + (double) Parameter.getItemNum() / runningTime
				+ " items/milliSecond ");
		System.out.println("Throught(link) : " + (double) Parameter.getLinkNum() / runningTime
				+ " links/milliSecond ");

		System.out.println("Communication: " + Parameter.getCommunicationNum());
		System.out.println("UsedMaxMemory: " + Parameter.getUsedMemory()*9.5367e-7+"Mb");

	}
}