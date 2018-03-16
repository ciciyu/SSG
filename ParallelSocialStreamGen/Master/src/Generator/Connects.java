package Generator;

import java.io.IOException;
import java.net.Socket;

public class Connects {
	Integer workerID;
	public Connects(Socket socket){
		Parameter.addOneConnectNum();
		try{
			IO io = new IO(socket);
			workerID = Parameter.workerIDGen.nextValue();
			Parameter.workerIO.put(workerID, io);
			io.writeWorkerInfo(Parameter.getWorkerInfo(workerID));
			System.out.println("Worker " + workerID +" connected");
		}catch (IOException e){
			try{
				socket.close();
			}catch(IOException e1){
				System.out.println("Error getting socket streams:"+e1);
			}
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Integer getSlaveID(){
		return workerID;
	}
}
	
	
	
	