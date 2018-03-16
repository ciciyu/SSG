package Generator;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Object.ItemCom;
import Object.TaskResult;
import Object.WorkerInfo;
import Object.WorkerTask;

public class IO {
	private Lock writeLock = new ReentrantLock();
	private Lock readLock = new ReentrantLock();

	private DataOutputStream os;
	private BufferedReader is;
	private Socket worker;

	public IO(Socket worker) {
		this.worker = worker;
		try {
			is = new BufferedReader(new InputStreamReader(worker.getInputStream()));
			os = new DataOutputStream(worker.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String readHandler() throws Exception {
		readLock.lock();
		try{
			return is.readLine();
		}finally{
			readLock.unlock();
		}
	}





	public WorkerInfo readWorkerInfo() {
		
		readLock.lock();
		WorkerInfo worker = new WorkerInfo();
		try{
			String[] infor = is.readLine().split(",");
			worker.setPath(infor[0]);
			worker.setStartTime(Long.valueOf(infor[1]));
			worker.setEndTime(Long.valueOf(infor[2]));
			worker.setUserNum(infor[3]);
			worker.setDataType(infor[4]);
			worker.setWorkerID(Integer.valueOf(infor[5]));
			worker.setWorkerNum(Integer.valueOf(infor[6]));
			worker.setWindowSize(Long.valueOf(infor[7]));
		}catch (IOException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}finally{
			readLock.unlock();
			
		}
		return worker;
	}
	
	

	public WorkerTask readWorkerTask() throws Exception {
		Parameter.addOneCommunication();
		readLock.lock();
		try{
			WorkerTask task = new WorkerTask();  
			String[] infor = is.readLine().split(",");
			task.setId(infor[0]);
			task.setTime(Long.valueOf(infor[1]));
			task.setUid(infor[2]);
			task.setLinkNum(Integer.valueOf(infor[3]));
			return task;
		}finally{
			readLock.unlock();
			
		}
		 
	}
	
	public void writeTaskResult(TaskResult tr) throws Exception {
		Parameter.addOneCommunication();
		writeLock.lock();
		try {
			os.writeBytes("TaskResult" + "\n");
			os.writeBytes(tr.toString() + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}
	
	

	
	public void writeSendItemsEnd() throws Exception {
		
		writeLock.lock();
		try {

			System.out.println("SendItemsEnd");
			os.writeBytes("SendItemsEnd" + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}

	public void writeSendTaskResultEnd() throws Exception {
		
		writeLock.lock();
		try {

			System.out.println("SendTaskResultEnd");
			os.writeBytes("SendTaskResultEnd" + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}
	
	public void close() throws IOException{
		is.close();
		os.close();
		worker.close();
	}

	public void writeItemCom(ItemCom sendItem) {
		// TODO Auto-generated method stub
		Parameter.addOneCommunication();
		writeLock.lock();
		try {
			os.writeBytes("ItemCom" + "\n");
			os.writeBytes(sendItem.toString()+ "\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			writeLock.unlock();
			
		}
	}
	
	public void writeStatistic(Double UsedMaxMemory) {
		// TODO Auto-generated method stub
		writeLock.lock();
		try {
			os.writeBytes("UsedMaxMemory" + "\n");
			os.writeBytes(UsedMaxMemory+ "\n");
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			writeLock.unlock();
			
		}
	}
	
}