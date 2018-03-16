package Measurement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Measurement.Object.DAG;
import Measurement.Object.MItem;

/**
 * @author Chengcheng Yu
 */
public class Measurements2 {
	String input;
	String output;
	String dataType;
	Integer linkGranularity;
	//
	String plus = "";

	public Measurements2(String input, String output, String dataType) {
		this.input = input;
		this.output = output;
		this.dataType = dataType;
		if (dataType.equals("patent")) {
			linkGranularity = 4838400;
		} else {
			linkGranularity = 3600;
		}
	}

	public void Actibities(String fileName) throws Exception {
		// Map<Integer,List<Integer>> day_hour= new
		// HashMap<Integer,List<Integer>>();
		// Map<Integer,Integer> week_day= new HashMap<Integer,Integer>();

		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();

		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while ((line = file.readLine()) != null) {
			String[] lineitems = line.split("\t");
			long time = new TimeUtil(lineitems[1]).getSeconds();
			long time1 = new TimeUtil("2000-01-30 00:00:00").getSeconds();
			long time2 = new TimeUtil("2000-02-06 00:00:00").getSeconds();
			if (time >= time1 && time < time2) {
				Calendar date = Calendar.getInstance();
				date.setTime(df.parse(lineitems[1]));
				// int hour_of_day=date.get(Calendar.HOUR_OF_DAY);
				// MUtil.addOneByKey(hour, hour_of_day);
				int day_of_week = date.get(Calendar.DAY_OF_WEEK);
				// System.out.println(day_of_week);
				MUtil.addOneByKey(day, day_of_week);
			}
			if (time >= time2) {
				break;
			}
		}
		file.close();
		Map<Integer, Double> reault = MUtil.normal(day);
		for (Integer key : reault.keySet()) {
			System.out.println(key + "\t" + reault.get(key));
		}
	}

	public void DeleteDuplicate(String fileName) throws Exception {

		Set<String> value = new HashSet<String>();

		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		while ((line = file.readLine()) != null) {
			if (!value.contains(line)) {
				value.add(line);
			}
		}
		file.close();
		MUtil.generateFile(value, input, fileName + "_DeleteDuplicate.txt");
	}

	public void getDistributions(String fileName) throws Exception {
		if (dataType.equals("patent")) {
			// getDAGDistribution(fileName);
			getPatentStreamingDistributions(fileName);
		} else {
			// getDAGSizeHeightDistribution(fileName);
			getWeiboStreamingDistributions(fileName);
			// getStreamDisWeibo(fileName);

		}

	}

	public void getStreamDisWeibo(String fileName) throws Exception {
		Map<String, Long> items_time = new HashMap<String, Long>();
		TreeMap<Integer, Integer> linkIntervals = new TreeMap<Integer, Integer>();// linkInterval,Num

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();

		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			if (num % 1000000 == 0) {
				System.out.println(line);
			}
			num++;
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();
			items_time.put(citing, time);

			if (lineitems.length > 3) {
				String cited = lineitems[3];
				if (items_time.containsKey(citing) && items_time.containsKey(cited)) {
					long time1 = items_time.get(citing);
					long time2 = items_time.get(cited);
					int linkInter = (int) Math.ceil(Double.valueOf(time1 - time2)
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}
				MUtil.addOneByKey(itemIndegree, cited);
			}

		}
		file.close();

		MUtil.generateFile(MUtil.normal(linkIntervals), output + fileName + "/",
				"linkIntervals.txt");

		Map<Integer, Integer> itemIndegreeDis = new HashMap<Integer, Integer>();
		for (String itemID : itemIndegree.keySet()) {
			MUtil.addOneByKey(itemIndegreeDis, itemIndegree.get(itemID));
		}
		MUtil.generateFile(MUtil.normal(itemIndegreeDis), output + fileName + "/",
				"itemIndegree.txt");

	}

	public void getDAGSizeHeightDistribution(String fileName) throws IOException, ParseException {
		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		while ((line = file.readLine()) != null) {
			num++;
			if (num % 100000 == 0) {
				System.out.println(line);
			}
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];

			for (int i = 3; i < lineitems.length; i++) {
				String cited = lineitems[i];
				pm.merege(citing, cited);
			}

			if (lineitems.length == 3) {
				HashSet<String> members = new HashSet<String>();
				members.add(citing);
				Integer dag_height = 0;
				DAG idag = new DAG(citing, members, dag_height);

				pm.addDag(idag);
				pm.getItems().put(citing, new MItem(idag.getId(), 0));
			}

		}
		file.close();
		for (String dagId : pm.dags.keySet()) {
			DAG dag = pm.getDag(dagId);
			MUtil.addOneByKey(DAGHeight, dag.getHeight());
			MUtil.addOneByKey(DAGSize, dag.getMembers().size());
		}
		MUtil.generateFile(MUtil.normal(DAGHeight), output + fileName + plus + "/",
				"DAGHeight.txt");
		MUtil.generateFile(MUtil.normal(DAGSize), output + fileName + plus + "/", "DAGSize.txt");

	}

	public void getPatentStreamingDistributions(String fileName) throws Exception {
		Map<String, Long> item_time = new HashMap<String, Long>();

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();
		Map<String, Integer> itemOutdegree = new HashMap<String, Integer>();
		TreeMap<String, Integer> itemsPerUser = new TreeMap<String, Integer>();
		TreeMap<Long, Integer> linkIntervals = new TreeMap<Long, Integer>();
		TreeMap<Integer, Integer> itemsIntervals = new TreeMap<Integer, Integer>();
		Map<String, Long> user_lastTime = new HashMap<String, Long>();
		Map<Integer, Integer> week_Num = new HashMap<Integer, Integer>();

		DAGManager pm = new DAGManager();


		String line = null;
		Long start = null, end = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		Integer iit = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			if (num % 10000 == 0) {
				System.out.println(line);
			}
			num++;
			String[] lineitems = line.split("\t");
			Calendar date = Calendar.getInstance();
			date.setTime(df.parse(lineitems[1]));
			int week = date.get(Calendar.WEEK_OF_YEAR);
			MUtil.addOneByKey(week_Num, week);

			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();

			if (start == null) {
				start = time;
			}
			end = time;
			String uid = lineitems[2];

			item_time.put(citing, time);
			MUtil.addZeroByKey(itemIndegree, citing);

			for (int i = 3; i < lineitems.length; i++) {
				String cited = lineitems[i];

				if (item_time.containsKey(cited)) {
					long linkInter = time - item_time.get(cited);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}
				MUtil.addOneByKey(itemIndegree, cited);
				pm.merege(citing, cited);
			}
			if (lineitems.length == 3) {
				HashSet<String> members = new HashSet<String>();
				members.add(citing);
				Integer dag_height = 0;
				DAG idag = new DAG(citing, members, dag_height);

				pm.addDag(idag);
				pm.getItems().put(citing, new MItem(idag.getId(), 0));
			}
			if (user_lastTime.containsKey(uid)) {
				int itemsInter = (int) Math.ceil((time - user_lastTime.get(uid)) / (7 * 24 * 3600));
				MUtil.addOneByKey(itemsIntervals, itemsInter);
			}
			user_lastTime.put(uid, time);

			MUtil.setKeyValue(itemOutdegree, citing, (lineitems.length - 3));
			MUtil.addOneByKey(itemsPerUser, uid);
			
			int it=(int)Math.floor(((double)end - (double)start) /(3600 * 24 * 365 * 10));
			if(it>0 && (iit==null || iit!=it) ){
				iit=it;
				String name=fileName+"_sub_"+it;
				out_patent( name,  start,  end,  itemOutdegree,
						 itemIndegree, week_Num,
						 itemsPerUser,  linkIntervals,
						 itemsIntervals,  pm);
			}
			
			
			

		}
		file.close();

		
	}
	
	public void out_patent(String fileName, Long start, Long end, Map<String, Integer> itemOutdegree,
			Map<String, Integer> itemIndegree,Map<Integer, Integer> week_Num,
			TreeMap<String, Integer> itemsPerUser, TreeMap<Long, Integer> linkIntervals,
			TreeMap<Integer, Integer> itemsIntervals, DAGManager pm) throws IOException{

		TreeMap<String, Double> postRatePerUser = new TreeMap<String, Double>();
		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();
		
		for (String user : itemsPerUser.keySet()) {
			postRatePerUser.put(user, (double) itemsPerUser.get(user) / (end - start));
		}
		for (String dagId : pm.dags.keySet()) {
			DAG dag = pm.getDag(dagId);
			MUtil.addOneByKey(DAGHeight, dag.getHeight());
			MUtil.addOneByKey(DAGSize, dag.getMembers().size());
		}
		MUtil.generateFile(MUtil.normal(DAGHeight), output + fileName + plus + "/",
				"DAGHeight.txt");
		MUtil.generateFile(MUtil.normal(DAGSize), output + fileName + plus + "/", "DAGSize.txt");
		MUtil.generateFile(MUtil.normal(week_Num), output + fileName + plus + "/", "weekly.txt");

		MUtil.generateFile(MUtil.getNormalDis(itemIndegree), output + fileName + plus + "/",
				"itemIndegree.txt");
		MUtil.generateFile(MUtil.getNormalDis(itemOutdegree), output + fileName + plus + "/",
				"itemOutdegree.txt");
		MUtil.generateFile(MUtil.getRateNormalDis(postRatePerUser), output + fileName + plus + "/",
				"userPostRate.txt");

		TreeMap<Integer, Double> linkIntervalsDis = getIntervalsDis(linkIntervals, linkGranularity);
		MUtil.generateFile(linkIntervalsDis, output + fileName + plus + "/", "linkIntervals.txt");

		MUtil.generateFile(MUtil.normal(itemsIntervals), output + fileName + plus + "/",
				"itemsIntervals.txt");
	}

	public void getWeiboStreamingDistributions(String fileName) throws Exception {
		Map<String, Long> item_time = new HashMap<String, Long>();

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();
		TreeMap<String, Integer> itemsPerUser = new TreeMap<String, Integer>();
		TreeMap<Integer, Integer> linkIntervals = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> itemsIntervals = new TreeMap<Integer, Integer>();
		Map<String, Long> user_lastTime = new HashMap<String, Long>();
		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();

		DAGManager pm = new DAGManager();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String line = null;
		Long start = null, end = null;
		Integer iit=null;
		
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			if (num % 10000 == 0) {
				System.out.println(line);
			}
			num++;
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();
			String uid = lineitems[2];

			Calendar date = Calendar.getInstance();
			date.setTime(df.parse(lineitems[1]));
			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
			int hour_of_day = date.get(Calendar.HOUR_OF_DAY);
			MUtil.addOneByKey(day, day_of_week);
			MUtil.addOneByKey(hour, hour_of_day);

			if (start == null) {
				start = time;
			}
			end = time;

			item_time.put(citing, time);
			if (lineitems.length > 3) {
				String cited = lineitems[3];

				if (item_time.containsKey(cited)) {
					int linkInter = (int) Math.ceil(Double.valueOf(time - item_time.get(cited))
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}
				MUtil.addOneByKey(itemIndegree, cited);
				pm.merege(citing, cited);
			}
			if (lineitems.length == 3) {
				HashSet<String> members = new HashSet<String>();
				members.add(citing);
				Integer dag_height = 0;
				DAG idag = new DAG(citing, members, dag_height);

				pm.addDag(idag);
				pm.getItems().put(citing, new MItem(idag.getId(), 0));
			}

			if (user_lastTime.containsKey(uid)) {
				int itemsInter = (int) Math.ceil(Double.valueOf(time - user_lastTime.get(uid))
						/ linkGranularity);

				MUtil.addOneByKey(itemsIntervals, itemsInter);
			}
			user_lastTime.put(uid, time);

			MUtil.addOneByKey(itemsPerUser, uid);

			int it=(int)Math.floor(((double)end - (double)start) /(3600 * 24 * 180));
			if(it>0 && (iit==null || iit!=it) ){
				iit=it;
				String name=fileName+"_sub_"+it;
				out(name, start, end, itemIndegree, itemsPerUser, linkIntervals, itemsIntervals,
						day, hour, pm);
			}
			

		}
		file.close();

	}

	public void out(String fileName, Long start, Long end, Map<String, Integer> itemIndegree,
			TreeMap<String, Integer> itemsPerUser, TreeMap<Integer, Integer> linkIntervals,
			TreeMap<Integer, Integer> itemsIntervals, TreeMap<Integer, Integer> day,
			TreeMap<Integer, Integer> hour, DAGManager pm) throws IOException {

		TreeMap<String, Double> userPostRate = new TreeMap<String, Double>();
		// TreeMap<String, Integer> itemsWithLinkPerUser = new TreeMap<String,
		// Integer>();
		TreeMap<String, Double> rePostProPerUser = new TreeMap<String, Double>();
		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();
		for (String user : itemsPerUser.keySet()) {
			userPostRate.put(user, (double) itemsPerUser.get(user) / (end - start));
		}
		// for (String user : itemsWithLinkPerUser.keySet()) {
		// rePostProPerUser.put(user, (double) itemsWithLinkPerUser.get(user) /
		// itemsPerUser.get(
		// user));
		// }
		for (String dagId : pm.dags.keySet()) {
			DAG dag = pm.getDag(dagId);
			MUtil.addOneByKey(DAGHeight, dag.getHeight());
			MUtil.addOneByKey(DAGSize, dag.getMembers().size());
		}
		DAGHeight.remove(0);

		MUtil.generateFile(MUtil.normal(DAGHeight), output + fileName + plus + "/",
				"DAGHeight.txt");
		MUtil.generateFile(MUtil.normal(DAGSize), output + fileName + plus + "/", "DAGSize.txt");
		MUtil.generateFile(MUtil.getNormalDis(itemsPerUser), output + fileName + plus + "/",
				"itemsPerUser.txt");

		MUtil.generateFile(MUtil.getNormalDis(itemIndegree), output + fileName + plus + "/",
				"itemIndegree.txt");

		MUtil.generateFile(MUtil.getRateNormalDis(userPostRate), output + fileName + plus + "/",
				"userPostRate.txt");
		MUtil.generateFile(MUtil.getRateNormalDis(rePostProPerUser), output + fileName + plus + "/",
				"userRePostPro.txt");

		MUtil.generateFile(MUtil.normal(linkIntervals), output + fileName + plus + "/",
				"linkIntervals.txt");
		MUtil.generateFile(MUtil.normal(itemsIntervals), output + fileName + plus + "/",
				"itemsIntervals.txt");
		MUtil.generateFile(MUtil.normal(day), output + fileName + plus + "/", "day.txt");
		MUtil.generateFile(MUtil.normal(hour), output + fileName + plus + "/", "hour.txt");
	}

	public TreeMap<Integer, Double> getIntervalsDis(TreeMap<Long, Integer> linkIntervals,
			long linkGranularity) throws IOException {
		TreeMap<Integer, Integer> result = new TreeMap<Integer, Integer>();
		for (Long time : linkIntervals.keySet()) {
			int key = (int) Math.ceil(Double.valueOf(time) / linkGranularity);
			// int key = (int) (mutil*linkGranularity);
			if (result.containsKey(key)) {
				result.put(key, result.get(key) + linkIntervals.get(time));
			} else {
				result.put(key, linkIntervals.get(time));
			}

		}

		return MUtil.normal(result);
	}

	public void getUserPRate(String fileName) throws IOException, ParseException {
		Map<String, Integer> user_items = new HashMap<String, Integer>();
		Map<Double, Integer> pRate = new TreeMap<Double, Integer>();
		Map<Double, Double> pRate1 = new TreeMap<Double, Double>();

		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName));
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			String user = lineitems[2];
			MUtil.addOneByKey(user_items, user);
		}
		file.close();
		long range = new TimeUtil("1999-12-28").getSeconds() - new TimeUtil("1963-01-01").getSeconds();
		for (String key : user_items.keySet()) {
			double rate = (double) user_items.get(key) / range;
			MUtil.addOneByKey(pRate, rate);
		}
		for (Double key : pRate.keySet()) {
			double rate = (double) pRate.get(key) / user_items.size();
			pRate1.put(key, rate);
		}
		MUtil.generateFile(pRate1, input, "userPRate.txt");

	}

	public void getSourcePantentData() throws IOException, ParseException {
		Map<String, Long> item_time = new HashMap<String, Long>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + "apat63_99.txt"));
		int i = 0;
		while ((line = file.readLine()) != null) {
			i++;
			if (i > 1) {
				String[] lineitems = line.split(",");
				String patentId = lineitems[0];
				Calendar date = Calendar.getInstance();
				date.setTime(df.parse("1960-01-01"));
				date.add(Calendar.DATE, Integer.valueOf(lineitems[2]));
				long time = date.getTimeInMillis() / 1000;
				String user = lineitems[6];

				if (!user.equals("")) {
					item_time.put(patentId, time);
				}
			}
		}
		file.close();
		System.out.println("read apat63_99.txt end");
		BufferedReader file1 = new BufferedReader(new FileReader(input + "cite75_99.txt"));
		int j = 0;
		Map<String, Set<String>> links = new HashMap<String, Set<String>>();

		String path = input + "Distributions/" + "linkNetwork.txt";
		BufferedWriter linkNetworkfile = new BufferedWriter(new FileWriter(path));
		while ((line = file1.readLine()) != null) {
			j++;
			if (j > 1) {
				String[] lineitems = line.split(",");
				String citing = lineitems[0];
				String cited = lineitems[1];
				if (item_time.containsKey(citing) && item_time.containsKey(cited)) {
					long time_citing = item_time.get(citing);
					long time_cited = item_time.get(cited);
					long linkInter = time_citing - time_cited;
					if (linkInter >= 0) {
						linkNetworkfile.write(citing + "\t" + cited);
						linkNetworkfile.newLine();

						if (links.containsKey(citing)) {
							links.get(citing).add(cited);
						} else {
							Set<String> items = new HashSet<String>();
							items.add(cited);
							links.put(citing, items);
						}
						if (!links.containsKey(cited)) {
							links.put(cited, new HashSet<String>());
						}
					}

				}
			}
		}
		item_time.clear();
		file1.close();
		linkNetworkfile.flush();
		linkNetworkfile.close();
		System.out.println("read cite75_99.txt end");

		file = new BufferedReader(new FileReader(input + "apat63_99.txt"));
		i = 0;
		Long startTime = null, endTime = null;
		Map<String, Integer> itemsPerUser = new HashMap<String, Integer>();
		int itemsNum = 0, linkNum = 0;
		String path1 = input + "Distributions/" + "socialStream_patent.txt";
		BufferedWriter socialStreamfile = new BufferedWriter(new FileWriter(path1));
		while ((line = file.readLine()) != null) {
			i++;
			if (i > 1) {
				// System.out.println(line);
				String[] lineitems = line.split(",");
				String patentId = lineitems[0];
				Calendar date = Calendar.getInstance();
				date.setTime(df.parse("1960-01-01"));
				date.add(Calendar.DATE, Integer.valueOf(lineitems[2]));
				long time = date.getTimeInMillis() / 1000;
				String user = lineitems[6];

				if (links.containsKey(patentId)) {
					itemsNum++;
					if (startTime == null) {
						startTime = time;
					}
					endTime = time;
					MUtil.addOneByKey(itemsPerUser, user);

					socialStreamfile.write(patentId + "\t" + new TimeUtil(time).getTime()
							+ "\t" + user);
					if (!links.get(patentId).isEmpty()) {
						for (String link : links.get(patentId)) {
							socialStreamfile.write("\t" + link);
							linkNum++;
						}
					}
					socialStreamfile.newLine();
				}
			}
		}
		file.close();
		socialStreamfile.flush();
		socialStreamfile.close();

		System.out.println("read cite75_99.txt end too");
		Map<Integer, Double> dis = MUtil.getNormalDis(itemsPerUser);

		path1 = input + "Distributions/" + "producerRate.txt";
		socialStreamfile = new BufferedWriter(new FileWriter(path1));
		for (Integer key : dis.keySet()) {
			socialStreamfile.write((double) key / (endTime - startTime) + "\t" + dis.get(key)
					/ 100);
			socialStreamfile.newLine();
		}
		socialStreamfile.flush();
		socialStreamfile.close();

		System.out.println("start Time:" + new TimeUtil(startTime).getSeconds()
				+ " end time:" + new TimeUtil(endTime).getSeconds());
		System.out.println("itemsNum= " + itemsNum + "linkNum=" + linkNum);
	}

	public void getDAGDistribution(String fileName) throws IOException, ParseException {
		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		String line = null;
		String path = input + fileName + "_linkNetwork.txt";
		BufferedReader file = new BufferedReader(new FileReader(path));

		while ((line = file.readLine()) != null) {
			if (line.contains("\"") || line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];
			String cited = lineitems[1];
			pm.merege(citing, cited);
		}
		file.close();
		for (String dagId : pm.dags.keySet()) {
			DAG dag = pm.getDag(dagId);
			MUtil.addOneByKey(DAGHeight, dag.getHeight());
			MUtil.addOneByKey(DAGSize, dag.getMembers().size());
		}
		MUtil.generateFile(MUtil.normal(DAGHeight), output + fileName + plus + "/",
				"DAGHeight.txt");
		MUtil.generateFile(MUtil.normal(DAGSize), output + fileName + plus + "/", "DAGSize.txt");

	}

	public void getInAndOutDegreeDistribution(String fileName) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		Map<String, Integer> in = new TreeMap<String, Integer>();
		Map<String, Integer> out = new TreeMap<String, Integer>();

		String line = null;
		while ((line = file.readLine()) != null) {
			if (line.contains("\"") || line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			if (!in.containsKey(lineitems[0])) {
				in.put(lineitems[0], 0);
			}
			if (in.containsKey(lineitems[1])) {
				in.put(lineitems[1], in.get(lineitems[1]) + 1);
			} else {
				in.put(lineitems[1], 1);
			}

			if (!out.containsKey(lineitems[1])) {
				out.put(lineitems[1], 0);
			}

			if (out.containsKey(lineitems[0])) {
				out.put(lineitems[0], out.get(lineitems[0]) + 1);
			} else {
				out.put(lineitems[0], 1);
			}
		}
		file.close();
		MUtil.generateFile(MUtil.getNormalDis(in), output, "inDeg.txt");
		MUtil.generateFile(MUtil.getNormalDis(out), output, "outDeg.txt");

	}

	public void getNormalization(String fileName) throws IOException {
		// java.text.DecimalFormat df = new java.text.DecimalFormat("#.######");
		double sum = 0;
		TreeMap<Integer, Double> tm = new TreeMap<Integer, Double>();
		BufferedReader rfile = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		while ((line = rfile.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			sum += Double.valueOf(lineitems[1]);
			tm.put(Integer.valueOf(lineitems[0]), Double.valueOf(lineitems[1]));

		}
		rfile.close();

		BufferedWriter wfile = new BufferedWriter(new FileWriter(output + fileName + "1.txt"));
		for (Integer key : tm.keySet()) {
			wfile.write(key + "\t" + tm.get(key) / sum * 100);
			wfile.newLine();
		}
		wfile.flush();
		wfile.close();
	}

	public void getPatentstreamInfo() throws IOException, ParseException {

		String line = null;

		Set<String> users = new HashSet<String>();
		BufferedReader file = new BufferedReader(new FileReader(input + "patentStream.txt"));
		int i = 0;
		int j = 0, userNum = 0;
		while ((line = file.readLine()) != null) {
			j++;
			String[] lineitems = line.split("\t");
			if (!users.contains(lineitems[2])) {
				userNum++;
				users.add(lineitems[2]);
			}
			if (lineitems.length > 3) {
				i++;
			}
		}
		file.close();
		System.out.println((double) (j - i) / j + "\t" + userNum + "\t" + j);

	}

	public void getPowerLawExponentData(String fileName) throws Exception, IOException {
		BufferedReader rfile = new BufferedReader(new FileReader(input + fileName + ".txt"));
		BufferedWriter wfile = new BufferedWriter(new FileWriter(output + fileName + ".txt"));
		String line = null;
		while ((line = rfile.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			int count = Integer.valueOf(lineitems[1]);
			for (int i = 0; i < count; i++) {
				wfile.write(lineitems[0]);
				wfile.newLine();
			}
		}
		rfile.close();
		wfile.flush();
		wfile.close();
	}

	public static void main(String[] args) throws Exception {
		 String input_gen =args[0];//"/Users/ycc/kuaipan/workspace/SocialStreamGen/out/";
//		String input_gen = "/Users/ycc/kuaipan/data/gnuplot_sourcedata/weibo/";
		String output_gen = input_gen + "Distribution/";
		String type =args[1];
		Measurements2 mm = new Measurements2(input_gen, output_gen, type);
		// mm.getDistributions(args[3]);
		// mm.Actibities("weibo_10000");
//		mm.DeleteDuplicate("producerNetwork_weibo");
		mm.getDistributions(args[2]);
	}

}
