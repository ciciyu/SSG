package Generator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Object.Item;
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
		try {
			return is.readLine();
		} finally {
			readLock.unlock();
		}
	}

	public void writeWorkerInfo(WorkerInfo info) throws Exception {
		
		writeLock.lock();
		try {
			os.writeBytes("WorkerInfo" + "\n");
			os.writeBytes(info.toString() + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}

	
	public TaskResult readTaskResult() throws Exception {
		// TODO Auto-generated method stub
		
		readLock.lock();
		try {
			String[] infor = is.readLine().split(",");
			Set<String> links = new HashSet<String>();
			for(int i=1;i<infor.length;i++){
				links.add(infor[i]);
			}
			TaskResult result = new TaskResult(); 
			result.setId(infor[0]);
			result.setLinks(links);
			return result;
		} finally {
			readLock.unlock();
			
		}
	}



	public void writeSendTaskEnd() throws IOException {
		// TODO Auto-generated method stub
		
		writeLock.lock();
		try {
			os.writeBytes("SendTaskEnd" + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}

	public Item readItemCom() throws Exception {
		
		readLock.lock();
		try {
			Item item = new Item();
			String[] infor = is.readLine().split(",");
			item.setId(infor[0]);
			item.setTime(Long.valueOf(infor[1]));
			item.setUid(infor[2]);
			item.setLinkNum(Integer.valueOf(infor[3]));
			Set<String> links = new HashSet<String>();
			for (int i = 4; i < infor.length; i++) {
				links.add(infor[i]);
			}
			item.setLinks(links);

			return item;
		} finally {
			readLock.unlock();
			
		}
	}

	public void close() throws IOException {
		is.close();
		os.close();
		worker.close();
	}

	/** 
	* @Title: writeWorkerTask 
	* @Description: TODO
	* @param @param wTask    
	* @return void    
	* @throws 
	*/
	public void writeWorkerTask(WorkerTask wTask)  throws Exception{
		// TODO Auto-generated method stub
		
		writeLock.lock();
		try {
			os.writeBytes("WorkerTask" + "\n");
			os.writeBytes(wTask.toString() + "\n");
			os.flush();
		} finally {
			writeLock.unlock();
			
		}
	}
	

}