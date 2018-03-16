package Generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import Common.generator.CounterGenerator;
import Generator.Util.GeneratorFactory;
import Generator.Util.LinkIntervalsGenerator;
import Generator.Util.OutDegreeGenerator;
import Generator.Util.ProducerProportyGenerator;
import Generator.Util.TimeUtil;
import Object.User;

public class Parameter {
	/* input parameters */
	public static Long startTime;
	public static Long endTime;
	public static String userNum;
	public static String dataType;
	public static String viewSize;
	public static Long windowSize;
	public static String path;
	public static Map<Integer, Set<Integer>> followerList;
	public static Long runningStart;
	public static TimeUtil timeUtil;
	public static Double beta;

	public static Long strart=null;
	public static Integer strartNum=null;

	/* file parameters */
	public static LinkIntervalsGenerator LinkIntervalsGen;
	public static OutDegreeGenerator ItemOutdegreeGen;
	public static ProducerProportyGenerator ProducerProportyGen;
//	public static RateGenerator RateGen;
	public static List<Double> hour;
	public static List<Double> day;
	public static List<Double> week;

	public static Map<Long,Integer> linkIntervalDis = new TreeMap<Long,Integer>();
	
	/* system parameters */
	public static Map<Integer, User> users = new HashMap<Integer, User>();
	public static CounterGenerator IDGen;
	public static double maxFactor = 1;
	public static double sum_pRate = 0;
	public static Integer linksize0 = 0;
	

	public static BufferPoolManager pool;// = new BufferPoolManager();

	/*
	 * Initialization of various parameters
	 */
	public Parameter(String path) throws Exception {
		this.path = path;
		initParameters();
		initGenerators();
		initSocialNetwork();
	}
	

	/*
	 * Initialized the input parameters
	 */
	private void initParameters() throws Exception {
		// TODO Auto-generated constructor stub
		Properties pro = new Properties();
		File f = new File(path + "properties.txt");
		if (f.exists()) {
			pro.load(new FileInputStream(f));
		} else {
			pro.setProperty("startTime", "1999-01-01 00:00:00");
			pro.setProperty("endTime", "2020-01-01 00:00:00");
			pro.setProperty("userNum", "2_11");
			pro.setProperty("path", path);
			pro.setProperty("cacheSize", "1048576");// 1024 * 1024
			pro.setProperty("windowSize", "172800");// 48*3600
			pro.setProperty("dataType", "patent");
			pro.store(new FileOutputStream(f), "FRUIT CLASS");
		}
		dataType = pro.getProperty("dataType");

		timeUtil = new TimeUtil(dataType);
		startTime = timeUtil.changeTimeToSeconds(pro.getProperty("startTime"));
		endTime = timeUtil.changeTimeToSeconds(pro.getProperty("endTime"));
		userNum = pro.getProperty("userNum");
		viewSize= pro.getProperty("viewSize");
//		beta= Double.valueOf(pro.getProperty("beta"));
		windowSize = Long.valueOf(pro.getProperty("windowSize"));
		pool = new BufferPoolManager();
		 
	}

	/**
	 * Initialization of generators from file parameters
	 */
	private void initGenerators() throws Exception {
		// TODO Auto-generated method stub

		GeneratorFactory genFactory = new GeneratorFactory();

		IDGen = genFactory.createItemIdentifierGenerator();

		String path_userProporty = path + "/data/" + dataType + "/producerProporty.txt";
		ProducerProportyGen = genFactory.createRateGenerator(path_userProporty);


		String path_linkIntervals = path + "/data/" + dataType + "/linkIntervals.txt";
		LinkIntervalsGen = genFactory.createLinkIntervalsGenerator(path_linkIntervals);
		
	

		String path_itemOutdegree = path + "/data/" + dataType + "/itemOutdegree.txt";
		ItemOutdegreeGen = genFactory.createOutDegreeGenerator(path_itemOutdegree);


		String weekPath = path + "/data/" + dataType + "/week.txt";
		initWeek(weekPath);
		
		String hourPath = path + "/data/" + dataType + "/hour.txt";
		initHour(hourPath);

		String dayPath = path + "/data/" + dataType + "/day.txt";
		initDay(dayPath);

		/* Initialized maxFactor for generating next item of each tweet */

	}

	/*
	 * Initialized adjusting coefficient of day from file, there are 7
	 * coefficients (the post count in day i of week)/(the post average count
	 * for each day of week) i=0...6
	 */
	private void initDay(String dayPath) throws IOException {
		String line = null;
		File f = new File(dayPath);
		if (f.exists()) {
			List<Double> days = new ArrayList<Double>();
			double sum = 0;
			double maxDay = 0;
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
			boolean first = true;
			for (double id : days) {
				double iday = id / average;
				this.day.add(iday);
				if (first) {
					first = false;
					maxDay = iday;
				} else {
					maxDay = iday > maxDay ? iday : maxDay;
				}
			}

			maxFactor *= maxDay;
		}

	}

	/*
	 * Initialized adjusting coefficient of hour from file, there are 24
	 * coefficients (the post count in hour i)/(the post count in hour 0)
	 * i=0...23
	 */
	private void initHour(String hourPath) throws IOException {
		String line = null;
		File f = new File(hourPath);
		if (f.exists()) {
			List<Double> hours = new ArrayList<Double>();
			double sum = 0;
			double maxHour = 0;
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

			boolean first = true;
			double average = sum / 24;
			for (int i = 0; i < hours.size(); i++) {
				double ihour = hours.get(i) / average;
				this.hour.add(ihour);
				if(first){
					maxHour = ihour;
					first=false;
				}else{
					maxHour = ihour > maxHour?ihour:maxHour;
				}
			}
			maxFactor *= maxHour;
		}

		
	}
	private void initWeek(String weekPath) throws IOException {
		String line = null;
		File f = new File(weekPath);
		if (f.exists()) {
			List<Double> weeks = new ArrayList<Double>();
			double sum = 0;
			double maxWeek = 0;
			week = new ArrayList<Double>();

			BufferedReader weekFile = new BufferedReader(new FileReader(f));
			while ((line = weekFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				double ihour = Double.valueOf(linesItem[1]);
				sum += ihour;
				weeks.add(ihour);
			}
			weekFile.close();

			boolean first = true;
			double average = sum / 53;
			for (int i = 0; i < weeks.size(); i++) {
				double iweek = weeks.get(i) / average;
				this.week.add(iweek);
				if(first){
					maxWeek = iweek;
					first=false;
				}else{
					maxWeek = iweek > maxWeek?iweek:maxWeek;
				}
			}
			maxFactor *= maxWeek;
		}

		
	}
	/*
	 * Initialized the social network from file of social network
	 */
	private void initSocialNetwork() throws IOException {
		String socialNetworkPath = path + "data/" + dataType + "/" + dataType + "_" + userNum
				+ ".txt"; 
//		String socialNetworkPath = path + "data/" + dataType + "/producerNetwork.txt" ;
		
		File f = new File(socialNetworkPath);
		String line = null;
		if (f.exists()) {
			followerList = new HashMap<Integer, Set<Integer>>();
			BufferedReader followerListFile = new BufferedReader(new FileReader(f));
			while ((line = followerListFile.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Integer uid1 = Integer.valueOf(linesItem[0]);
				Integer uid2 = Integer.valueOf(linesItem[1]);


				initUsers(uid1);
				initUsers(uid2);

				if (followerList.containsKey(uid1)) {
					followerList.get(uid1).add(uid2);
				} else {
					Set<Integer> members = new HashSet<Integer>();
					members.add(uid2);
					followerList.put(uid1, members);
				}
				
				if (followerList.containsKey(uid2)) {
					if(!followerList.get(uid2).contains(uid1)){
						followerList.get(uid2).add(uid1);
					}
				} else {
					Set<Integer> members = new HashSet<Integer>();
					members.add(uid1);
					followerList.put(uid2, members);
				}
				

			}
			followerListFile.close();
			

			linksize0 = (int) (sum_pRate * (endTime - startTime) * 0.17);

		} else {
			System.err.println("Do not have the file: " + socialNetworkPath);
		}
		ProducerProportyGen.clear();
		
	}

	/*
	 * Initialized post rate, repost rate of a user
	 */
	private void initUsers(Integer uid) {
		if (!users.containsKey(uid)) {
			users.put(uid, new User(uid, 0));
			String[] proporty = ProducerProportyGen.nextValue().split("\t");

			if (dataType.equals("weibo") ||dataType.equals("email")) {
				double rpRate = Double.valueOf(proporty[0]);
				double pRate = Double.valueOf(proporty[1]);
				users.put(uid, new User(uid, pRate, rpRate));
			} else {
				double pRate = Double.valueOf(proporty[0]);
				users.put(uid, new User(uid, pRate));
				sum_pRate += pRate;
			}

		}
	}

	
}