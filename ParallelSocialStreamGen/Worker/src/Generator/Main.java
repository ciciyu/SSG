package Generator;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;


public class Main{
	
	public static void main(String args[]) throws Exception{	
		try{
			 new Parameter(args[0],Integer.valueOf(args[1]),args[2],args[3]);
			long start = System.currentTimeMillis();
			CountDownLatch threadSignal = new CountDownLatch(3);
			new Thread(new SSSGenerator(threadSignal)).start();
			new Thread(new ReceiveTask(threadSignal)).start();
			new Thread(new SendTaskResult(threadSignal)).start();
			
			threadSignal.await();
			
			Parameter.io.close();
			
			long end = System.currentTimeMillis();
			System.out.println("Worker " + Parameter.workerInfo.getWorkerID()+"generate data end");
			System.out.println("Total itemsNum ="+ Parameter.IDGen.lastValue());
			System.out.println("running time: "+ (end - start) );
			System.out.println("Throughput: "+ (double)Parameter.IDGen.lastValue()/(end - start) 
					+"items/Millisecond");
			
			System.out.println("Communication: " + Parameter.getCommunicationNum());
			System.out.println("UsedMaxMemory: " + Parameter.getUsedMemory()*9.5367e-7+"Mb");
			
		}catch(UnknownHostException el){
			System.out.println("Unknown Host:"+el);
		}catch(IOException el){
			System.out.println("Error io:"+el);
		}
	}
}