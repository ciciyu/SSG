package Generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Common.Generator.CounterGenerator;
import Generator.Util.AbstractGenerator;
import Generator.Util.GeneratorFactory;
import Generator.Util.LinkIntervalsGenerator;
import Generator.Util.OutDegreeGenerator;
import Generator.Util.TimeUtil;
import Object.Friends;
import Object.User;
import Object.WorkerInfo;

public class Parameter {
	private static int REMOTE_PORT ;
	public static String IP;
	public static IO io;

//	public static int cutLength;
	public static String viewSize;
	
	/* input parameters */
	public static WorkerInfo workerInfo;
	public static Map<String, Friends> followerList;


	/* file parameters */
	public static LinkIntervalsGenerator LinkIntervalsGen;
	public static OutDegreeGenerator ItemOutdegreeGen;
	public static List<Double> hour;
	public static List<Double> day;

	/* system parameters */
	public static Map<String, User> users = new HashMap<String, User>();;
	public static CounterGenerator IDGen;
	public static Double maxFactor = 1.0;
	public static Integer linksize0 = 0;

	public static BufferPoolManager pool = new BufferPoolManager();
	private static String partitionPath;
	private static String parameterPath;

	public static TaskManager tasks = new TaskManager();
	private static ConcurrentHashMap <String,Integer> itemIndegree = new ConcurrentHashMap <String,Integer>();
	public static TimeUtil timeUtil;
	public static AbstractSSSGenTool genTool;
	
	public static boolean isSendTaskEnd= false;
	

	private static int communicationNum = 0;
	public static long usedMemory=0;
	/*
	 * Initialization of various parameters
	 */
	public Parameter(String ip,int port,String workerPath,String viewSize) throws Exception {
		this.REMOTE_PORT =port;
		this.viewSize =viewSize;
		IP = ip;
		initConnection();
		initParameters(workerPath);
		initGenerators();
		initUsers();
		initSocialNetwork();
		System.out.println("Worker " + workerInfo.getWorkerID() + " parameter end.");
	}
	
	public static Integer getItemIndegree(String itemID){
		int weight =1;
		if(itemIndegree.containsKey(itemID)){
			weight=itemIndegree.get(itemID);
		}
		return weight;
	}
	public static void addOneItemIndegree(String itemID){
		if(itemIndegree.containsKey(itemID)){
			itemIndegree.put(itemID, itemIndegree.get(itemID)+1);
		}else{
			itemIndegree.put(itemID, 2);
		}
	}

	private void initConnection() {
		// TODO Auto-generated method stub
		try {
			io = new IO(new Socket(IP, REMOTE_PORT));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * Initialized the input parameters
	 */
	private void initParameters(String workerPath) throws IOException {
		// TODO Auto-generated constructor stub
		String Handler;
		try {
			Handler = io.readHandler();
			if (Handler.equals("WorkerInfo")) {
				workerInfo = io.readWorkerInfo();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		workerInfo.setPath(workerPath);
		parameterPath = workerInfo.getPath() + "data/" + workerInfo.getDataType() + "/";
		partitionPath = parameterPath + workerInfo.getUserNum() + "_par" + workerInfo.getWorkerNum() + "/";
		timeUtil = new TimeUtil(workerInfo.getDataType());
		genTool = SSSGenToolProvider.createSSSGenTool();
	}

	/**
	 * Initialization of generators from file parameters
	 */
	private void initGenerators() throws Exception {
		// TODO Auto-generated method stub

		AbstractGenerator genFactory = new GeneratorFactory();

		IDGen = genFactory.createItemIdentifierGenerator();

		String path_linkIntervals = parameterPath + "linkIntervals.txt";
		LinkIntervalsGen = genFactory.createLinkIntervalsGenerator(path_linkIntervals);

		if (workerInfo.getDataType().equals("weibo")) {
			String hourPath = parameterPath + "hour.txt";
			Double maxHour = initHour(hourPath);

			String dayPath = parameterPath + "day.txt";
			Double maxDay = initDay(dayPath);

			/* Initialized maxFactor for generating next item of each tweet */
			if (maxHour != null || maxDay != null) {
				maxFactor = maxHour * maxDay;
			}
		} else {
			String path_itemOutdegree = parameterPath + "itemOutdegree.txt";
			ItemOutdegreeGen = genFactory.createOutDegreeGenerator(path_itemOutdegree);
		}
	}

	/*
	 * Initialized adjusting coefficient of day from file, there are 7
	 * coefficients (the post count in day i of week)/(the post average count
	 * for each day of week) i=0...6
	 */
	private Double initDay(String dayPath) throws IOException {
		Double maxDay = null;
		String line = null;
		File f = new File(dayPath);
		if (f.exists()) {
			List<Double> days = new ArrayList<Double>();
			double sum = 0;
			maxDay = 0.0;
			day = new ArrayList<Double>();
			BufferedReader dayFile = new BufferedReader(new FileReader(f));

			while ((line = dayFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				double iday = Double.valueOf(linesItem[1]);
				sum += iday;
				days.add(iday);
			}
			dayFile.close();

			double average = sum / 7;
			for (double id : days) {
				double iday = id / average;
				Parameter.day.add(iday);
				if (iday > maxDay) {
					maxDay = iday;
				}
			}
		}
		return maxDay;
	}

	/*
	 * Initialized adjusting coefficient of hour from file, there are 24
	 * coefficients (the post count in hour i)/(the post count in hour 0)
	 * i=0...23
	 */
	private Double initHour(String hourPath) throws IOException {
		Double maxHour = null;
		String line = null;
		File f = new File(hourPath);
		if (f.exists()) {
			List<Double> hours = new ArrayList<Double>();
			double sum = 0;
			maxHour = 0.0;
			hour = new ArrayList<Double>();

			BufferedReader hourFile = new BufferedReader(new FileReader(f));
			while ((line = hourFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				double ihour = Double.valueOf(linesItem[1]);
				sum += ihour;
				hours.add(ihour);
			}
			hourFile.close();

			double average = sum / 24;
			for (int i = 0; i < hours.size(); i++) {
				double ihour = hours.get(i) / average;
				Parameter.hour.add(ihour);
				if (ihour > maxHour) {
					maxHour = ihour;
				}
			}
		}

		return maxHour;
	}

	/*
	 * Initialized the social network from file of social network
	 */
	private void initSocialNetwork() throws IOException {
		String socialNetworkPath = partitionPath + "subNetwork_" + workerInfo.getWorkerID() + ".txt";
		File f = new File(socialNetworkPath);
		String line = null;
		if (f.exists()) {
			followerList = new HashMap<String, Friends>();
			BufferedReader followerListFile = new BufferedReader(new FileReader(f));
			while ((line = followerListFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Friends fr = new Friends();
//				fr.setSumRate(Double.valueOf(linesItem[1]));
				fr.set_pro(Double.valueOf(linesItem[1]));
				for (int i = 2; i < linesItem.length; i++) {
					fr.addMember(linesItem[i]);
					if(!users.containsKey(linesItem[i])){
						System.out.println("");
					}
				}
				followerList.put(linesItem[0], fr);
			}
			followerListFile.close();

		} else {
			System.err.println("Do not have the file: " + socialNetworkPath);
		}

	}

	private void initUsers() throws IOException {
		String usersPath = partitionPath + "/userInformation_" + workerInfo.getWorkerID() + ".txt";
		File f = new File(usersPath);
		String line = null;
		double sum = 0;
		if (f.exists()) {
			BufferedReader userFile = new BufferedReader(new FileReader(f));
			while ((line = userFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				String uid = linesItem[0];

				if (workerInfo.getDataType().equals("patent")) {
					double pRate = Double.valueOf(linesItem[1]);
					sum += pRate;
					users.put(uid, new User(uid, pRate));
				} else {
					double rpRate = Double.valueOf(linesItem[1]);
					double pRate = Double.valueOf(linesItem[2]);
					sum += pRate;
					users.put(uid, new User(uid, pRate, rpRate));
				}

			}
			userFile.close();
			linksize0 = (int) (sum * (workerInfo.getEndTime() - workerInfo.getStartTime()) * 0.18);
		} else {
			System.err.println("Do not have the file: " + usersPath);
		}
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
	
}