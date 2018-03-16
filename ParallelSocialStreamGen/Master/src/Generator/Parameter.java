package Generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import Common.generator.CounterGenerator;
import Generator.Util.TimeUtil;
import Object.Item;
import Object.WorkerInfo;

public class Parameter {
	/* input parameters */

	public static int TESTPORT;

	public static long startTime;
	public static long endTime;
	public static String userNum;
	public static int workerNum;
	public static String workerPath;
	public static String masterPath;
	public static String dataType;
	public static int itemsSizeInWorker;
	public static String viewSize;
//	public static int resultSize;
	
	public static TimeUtil timeUtil;

	public static ConcurrentHashMap<Integer, IO> workerIO = new ConcurrentHashMap<Integer, IO>();
	public static CounterGenerator workerIDGen = new CounterGenerator(0);;
	public static Map<String, HashMap<Integer, Double>> followInfo = new HashMap<String, HashMap<Integer, Double>>();
	// <uid,<slaveID,pRate>>
	
//	public static BufferedWriter streamfile;
//	public static BufferedWriter linkfile;
	public static BufferedWriter outfile;
//	public static BufferedWriter logfile;
	String parameterPath;

	public static Long windowSize;


	public static ItemManager items ;
	public static TaskManager tasks= new TaskManager();
	public static ConcurrentSkipListSet<Integer> generateItemEndWorkers = new ConcurrentSkipListSet<Integer>();
	public static ConcurrentSkipListSet<Integer> sendTaskResultEndWorkers = new ConcurrentSkipListSet<Integer>();
	public static TaskResultManager taskResults = new TaskResultManager();
//	public static ConcurrentHashMap<String, Integer> taskAllocate = new ConcurrentHashMap<String, Integer>();
	public static ConcurrentHashMap<String, Set<Integer>> taskAllocate = new ConcurrentHashMap<String, Set<Integer>>();

	public static ConcurrentSkipListSet<Integer> waitResultWorker = new ConcurrentSkipListSet<Integer>();
	/*
	 * Initialization of various parameters
	 */
	
	public static int connectNum = 0;
	private static int itemNum = 0;
	public static int linkNum = 0;
	private static int communicationNum = 0;
	public static long usedMemory=0;
	
	
	public Parameter(String path, int port) throws Exception {
		Parameter.TESTPORT= port;
		System.out.println("Master parameter start.");
		initParameters(path);
		initSocialNetwork();
		System.out.println("Master parameter end.");
	}
	

	
//	public static void addTaskAllocatePlan(String id, Integer workerNum) {
//		if (!Parameter.taskAllocate.containsKey(id)) {
//			Parameter.taskAllocate.put(id, workerNum);
//		}
//
//	}

	
	
	public static synchronized int getLinkNum() {
		return linkNum;
	}



	public static synchronized void addLinkNum(int linkNum) {
		Parameter.linkNum += linkNum;
	}



	public static synchronized long getUsedMemory() {
		return usedMemory;
	}



	public static synchronized void setUsedMemory(long usedMemory) {
		if(usedMemory>Parameter.usedMemory){
			Parameter.usedMemory = usedMemory;
		}
		
	}
	
	public synchronized static int getCommunicationNum() {
		return communicationNum;
	}

	public synchronized static void addOneCommunication() {
		communicationNum++;
	}

	public synchronized static int getItemNum() {
		return itemNum;
	}

	public synchronized static void addOneItem() {
		itemNum++;
	}

	/*
	 * Initialized the input parameters
	 */
	private void initParameters(String path) throws Exception {
		// TODO Auto-generated constructor stub
		Properties pro = new Properties();
		File f = new File(path + "/properties.txt");
		if (f.exists()) {
			pro.load(new FileInputStream(f));
		} else {
			pro.setProperty("startTime", "1999-01-01 00:00:00");
			pro.setProperty("endTime", "2020-01-01 00:00:00");
			pro.setProperty("userNum", "2_11");
			pro.setProperty("workerNum", "2");
			pro.setProperty("workerPath", "/Users/ycc/kuaipan/workspace/ParallelSSSGen/src/Worker/");
			pro.setProperty("masterPath", "/Users/ycc/kuaipan/workspace/ParallelSSSGen/src/Master/");
			pro.setProperty("dataType", "weibo");
			pro.setProperty("ip", "192.168.1.102");
			pro.store(new FileOutputStream(f), "FRUIT CLASS");
		}
		dataType = pro.getProperty("dataType");
		timeUtil = new TimeUtil(dataType);
		startTime = timeUtil.changeTimeToSeconds(pro.getProperty("startTime"));
		endTime = timeUtil.changeTimeToSeconds(pro.getProperty("endTime"));
		userNum = pro.getProperty("userNum");
		workerNum = Integer.valueOf(pro.getProperty("workerNum"));
		workerPath = pro.getProperty("workerPath");
		masterPath = pro.getProperty("masterPath");
		itemsSizeInWorker=  Integer.valueOf(pro.getProperty("itemsSizeInWorker"));
		viewSize = pro.getProperty("viewSize");
		windowSize =Long.valueOf(pro.getProperty("windowSize")) ;
		
		String outputPath = masterPath + "out/";
		File file = new File(outputPath);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		outfile = new BufferedWriter(new FileWriter(outputPath + dataType + "_" + userNum + "_par" + workerNum + ".txt"));
//		logfile = new BufferedWriter(new FileWriter(outputPath + dataType + "_" + userNum + "_par" + workerNum + "_log.txt"));
		
//		streamfile = new BufferedWriter(new FileWriter(outputPath + dataType + "_" + userNum + "_par" + workerNum + "_stream.txt"));
//		linkfile = new BufferedWriter(new FileWriter(outputPath + dataType + "_" + userNum + "_par" + workerNum + "_linkNetwork.txt"));
		parameterPath = masterPath + "data/" + dataType + "/";
		
		items = new ItemManager();

	}

	

	/*
	 * Initialized the social network from file of social network
	 */
	private void initSocialNetwork() throws IOException {
		String socialNetworkPath = parameterPath + userNum + "_par" + workerNum + "/socialNetworkInWorkers.txt";
		File f = new File(socialNetworkPath);
		String line = null;
		if (f.exists()) {

			BufferedReader followerListFile = new BufferedReader(new FileReader(f));
			while ((line = followerListFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				HashMap<Integer, Double> workerInfo = new HashMap<Integer, Double>();
				for (int i = 1; i < linesItem.length; i++) {
					workerInfo.put(Integer.valueOf(linesItem[i]), Double.valueOf(linesItem[(++i)]));
				}
				followInfo.put(linesItem[0], workerInfo);
			}
			followerListFile.close();
		} else {
			System.err.println("Do not have the file: " + socialNetworkPath);
		}

	}

	public static WorkerInfo getWorkerInfo(Integer workerID) {
		WorkerInfo info = new WorkerInfo();
		info.setDataType(dataType);
		info.setStartTime(startTime);
		info.setEndTime(endTime);
		info.setWorkerID(workerID);
		info.setPath(workerPath);
		info.setWorkerNum(workerNum);
		info.setUserNum(userNum);
		info.setWindowSize(windowSize);
		return info;
	}

	public synchronized static int getConnectNum() {
		return connectNum;
	}

	public synchronized static void addOneConnectNum() {
		connectNum++;
	}

	public static void outToFile(Item item) {
		try {

			outfile.write(item.toOut());
			if(item.getLinks()!=null){
				for(String link:item.getLinks()){
					outfile.write("\t"+link);
				}
			}
			
			outfile.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	public synchronized static void outToLogFile(String message) {
//		try {
//
//			logfile.write(message);
//			logfile.newLine();
//			logfile.flush();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	public static void outStreamToFile(Item item) {
//		try {
//
//			streamfile.write(item.toOut());
//			streamfile.newLine();
//			
//			if(!item.getLinks().isEmpty()){
//				linkfile.write(item.getId());
//				for(String link:item.getLinks()){
//					linkfile.write("\t"+link);
//					linkfile.newLine();
//				}
//			}
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public static void outLinkToFile(TaskResult result) {
//		try {
//
//			linkfile.write(result.getId());
//			for(String link:result.getLinks()){
//				linkfile.write("\t"+link);
//				linkfile.newLine();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public static void closeFile() {
		try {
//			streamfile.flush();
//			streamfile.close();
//			linkfile.flush();
//			linkfile.close();
			outfile.flush();
			outfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}