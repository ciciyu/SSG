package Measurement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import Measurement.Object.DAG;
import Measurement.Object.MItem;

/**
 * @author Chengcheng Yu
 */
public class Measurements {
	String input;
	String output;
	String dataType;
	Integer linkGranularity;
	//
	String plus = "";

	public Measurements(String input, String output, String dataType) {
		this.input = input;
		this.output = output;
		this.dataType = dataType;
		if (dataType.equals("patent")) {
			linkGranularity = 3600*24;//4838400;
		} else if (dataType.equals("weibo")){
			linkGranularity = 3600*24;
		}if (dataType.equals("email")){
			linkGranularity = 3600*24;
		}
	}
	
	public void Actibities(String fileName) throws Exception{
//		Map<Integer,List<Integer>> day_hour= new HashMap<Integer,List<Integer>>();
//		Map<Integer,Integer> week_day= new HashMap<Integer,Integer>();
		

		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();
		
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while ((line = file.readLine()) != null) {
			String[] lineitems = line.split("\t");
			long time =Long.valueOf(lineitems[1]);
			long time1=new TimeUtil("2002-01-01 00:00:00").getSeconds();
			long time2=new TimeUtil("2003-01-01 00:00:00").getSeconds();
			Calendar date = Calendar.getInstance();
			date.setTime(new Date(time*1000));
//			int hour_of_day=date.get(Calendar.HOUR_OF_DAY);
//			MUtil.addOneByKey(hour, hour_of_day);
			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
//			System.out.println(day_of_week);
			MUtil.addOneByKey(day, day_of_week);
//			if(time>=time1 &&time<time2){
//				Calendar date = Calendar.getInstance();
//				date.setTime(new Date(time*1000));
////				int hour_of_day=date.get(Calendar.HOUR_OF_DAY);
////				MUtil.addOneByKey(hour, hour_of_day);
//				int day_of_week = date.get(Calendar.HOUR_OF_DAY);
////				System.out.println(day_of_week);
//				MUtil.addOneByKey(day, day_of_week);
//			}
			
		}
		file.close();
		Map<Integer,Double> reault=MUtil.normal(day);
		for(Integer key:reault.keySet()){
			System.out.println(key+"\t"+reault.get(key));
		}
	}
	
	
	public void ActibitiesEmail(String fileName) throws Exception{
//		Map<Integer,List<Integer>> day_hour= new HashMap<Integer,List<Integer>>();
//		Map<Integer,Integer> week_day= new HashMap<Integer,Integer>();
		

		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> week = new TreeMap<Integer, Integer>();
		
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		Long strat=null,end=null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while ((line = file.readLine()) != null) {
			String[] lineitems = line.split("\t");
			long time =Long.valueOf(lineitems[1]);
			if(strat==null){
				strat=end=time;
			}
			if(time<strat){
				strat=time;
			}
			if(time>end){
				end = time;
			}
			
//			Calendar date = Calendar.getInstance();
//			date.setTime(df.parse(lineitems[1]));
//			int hour_of_day=date.get(Calendar.HOUR_OF_DAY);
//			MUtil.addOneByKey(hour, hour_of_day);
//			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
//			MUtil.addOneByKey(day, day_of_week);
//			int week_of_year = date.get(Calendar.WEEK_OF_YEAR);
//			MUtil.addOneByKey(week, week_of_year);
		}
		System.out.println(new TimeUtil(strat).getTime());
		System.out.println(new TimeUtil(end).getTime());
		
		file.close();
//		Map<Integer,Double> reault=MUtil.normal(day);
//		for(Integer key:reault.keySet()){
//			System.out.println(key+"\t"+reault.get(key));
//		}
	}
	
	
	public void DeleteDuplicate(String fileName) throws Exception{
		

		Set<String> value = new HashSet<String>();
		
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		String line = null;
		while ((line = file.readLine()) != null) {
			if(!value.contains(line)){
				value.add(line);
			}
		}
		file.close();
		MUtil.generateFile(value, input, fileName+"_DeleteDuplicate.txt");
	}
	
	

	public void getDistributions(String fileName,boolean flag) throws Exception {
		if (dataType.equals("patent")) {
			// getDAGDistribution(fileName);
			getPatentStreamingDistributions(fileName);
		} else if(dataType.equals("weibo")) {
//			 getDAGSizeHeightDistribution(fileName);
			
//			 getStreamDisWeibo(fileName);
			if(flag){
				getWeiboStreamingDistributions2(fileName);
			}else{
				getWeiboStreamingDistributions(fileName);
			}
			 
		}else if(dataType.equals("email")) {
			getWeiboStreamingDistributions(fileName);
//			getEmailStreamingDistributions(fileName);
			 
		}

	}
	
	public void getStreamDisWeibo(String fileName) throws Exception{
		Map<String,Long> items_time = new HashMap<String,Long>();
		TreeMap<Integer, Integer> linkIntervals = new TreeMap<Integer, Integer>();//linkInterval,Num
		
		Map<String,Integer> itemIndegree = new HashMap<String,Integer>();
		
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
			
			if(lineitems.length>3){
				String cited = lineitems[3];
				if(items_time.containsKey(citing)&&items_time.containsKey(cited)){
					long time1= items_time.get(citing);
					long time2= items_time.get(cited);
					int linkInter = (int) Math.ceil(Double.valueOf(time1 - time2)
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}
				MUtil.addOneByKey(itemIndegree, cited);
			}
			
			

		}
		file.close();
		
		
		MUtil.generateFile(MUtil.normal(linkIntervals), output + fileName +  "/",
				"linkIntervals.txt");
		
		Map<Integer, Integer> itemIndegreeDis = new HashMap<Integer, Integer>();
		for(String itemID:itemIndegree.keySet()){
			MUtil.addOneByKey(itemIndegreeDis, itemIndegree.get(itemID));
		}
		MUtil.generateFile(MUtil.normal(itemIndegreeDis), output + fileName +  "/", "itemIndegree.txt");
		
	}

	public void getDAGSizeHeightDistribution(String fileName) throws IOException, ParseException {
		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num=0;
		while ((line = file.readLine()) != null) {
			num++;
			if(num%100000==0){
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
		TreeMap<String, Double> postRatePerUser = new TreeMap<String, Double>();
		TreeMap<Integer, Integer> itemsIntervals = new TreeMap<Integer, Integer>();
		Map<String, Long> user_lastTime = new HashMap<String, Long>();
		Map<Integer, Integer> week_Num = new HashMap<Integer, Integer>();
		
		
		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		String line = null;
		Long start = null, end = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
//			if (num % 10000 == 0) {
//				System.out.println(line);
//			}
			num++;
			String[] lineitems = line.split("\t");
			Calendar date = Calendar.getInstance();
			date.setTime(df.parse(lineitems[1]));
//			date.setTime(new Date(Long.valueOf(lineitems[1])*1000));
			int week=date.get(Calendar.WEEK_OF_YEAR);
			MUtil.addOneByKey(week_Num, week);
			
			
			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();
//Long time = Long.valueOf(lineitems[1]);
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
				int itemsInter =(int) Math.ceil((time - user_lastTime.get(uid))/ (7*24*3600));
				MUtil.addOneByKey(itemsIntervals, itemsInter);
			}
			user_lastTime.put(uid, time);

			MUtil.setKeyValue(itemOutdegree, citing, (lineitems.length - 3));
			MUtil.addOneByKey(itemsPerUser, uid);

		}
		file.close();

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
		MUtil.generateFile(MUtil.normal(week_Num), output+ fileName + plus + "/", "weekly.txt");

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
	
	public void paixu(String fileName) throws NumberFormatException, IOException{
		String line = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		TreeMap<Long,Set<String>> paixu= new TreeMap<Long,Set<String>>();
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			Long time = Long.valueOf(lineitems[1]);
			if(paixu.containsKey(time)){
				paixu.get(time).add(line);
			}else{
				Set<String> values= new HashSet<String>();
				values.add(line);
				paixu.put(time, values);
			}
		}
		file.close();
		BufferedWriter wfile = new BufferedWriter(new FileWriter(output + fileName + "1.txt"));
		for (Long key : paixu.keySet()) {
			Set<String> values = paixu.get(key);
			for(String value:values){
				wfile.write(value);
				wfile.newLine();
			}
			
		}
		wfile.flush();
		wfile.close();
		
	}
	
	public void getEmailStreamingDistributions(String fileName) throws Exception {
		Map<String, Long> item_time = new HashMap<String, Long>();
		Map<String, String> item_user = new HashMap<String, String>();

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();
		TreeMap<String, Integer> itemsPerUser = new TreeMap<String, Integer>();
		TreeMap<String, Integer> rtitemsPerUser = new TreeMap<String, Integer>();
		TreeMap<Long, Integer> linkIntervals = new TreeMap<Long, Integer>();
		TreeMap<String, Double> postRatePerUser = new TreeMap<String, Double>();
		TreeMap<Long, Integer> itemsIntervals = new TreeMap<Long, Integer>();
		Map<String,TreeSet<Long>> user_times = new HashMap<String,TreeSet<Long>>();
		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();
		
		Map<Integer, Integer> week_Num = new HashMap<Integer, Integer>();
		
//		Set<String> producerNetwork= new HashSet<String>();
		
		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		String line = null;
		Long start = null, end = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}
//			if (num % 10000 == 0) {
//				System.out.println(line);
//			}
			num++;
			String[] lineitems = line.split("\t");
			Calendar date = Calendar.getInstance();
			date.setTime(df.parse(lineitems[1]));
//			date.setTime(new Date(Long.valueOf(lineitems[1])*1000));
			int week=date.get(Calendar.WEEK_OF_YEAR);

			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
			int hour_of_day = date.get(Calendar.HOUR_OF_DAY);
			MUtil.addOneByKey(day, day_of_week);
			MUtil.addOneByKey(hour, hour_of_day);
			MUtil.addOneByKey(week_Num, week);
			
			
			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();
//Long time = Long.valueOf(lineitems[1]);
			if (start == null) {
				start = end= time;
			}
			if(time<start){
				start=time;	
			}
			if(time>end){
				end = time;
			}
			
			
			String uid = lineitems[2];
			
			item_user.put(citing, uid);
			item_time.put(citing, time);
			MUtil.addZeroByKey(itemIndegree, citing);

			for (int i = 3; i < lineitems.length; i++) {
				String cited = lineitems[i];

				if (item_time.containsKey(cited)) {
//					long linkInter = (long) Math.ceil(Double.valueOf(Math.abs(time - item_time.get(cited)))
//							/ 3600);
					
					long linkInter = Math.abs(time - item_time.get(cited));
					
					MUtil.addOneByKey(linkIntervals, linkInter);
					
					
				}
				String rtuid="";
				if(item_user.containsKey(cited)){
					rtuid= item_user.get(cited);
					
				}else{
					for(int j=0;j<cited.length();j++){
						if(Character.isDigit(cited.charAt(j))){
							rtuid+=cited.charAt(j);
						}else{
							break;
						}
					}
				}
//				String producerInfo= uid+"\t"+rtuid;
//				String producerInfo1= rtuid+"\t"+uid;
//				if(!producerNetwork.contains(producerInfo) && !producerNetwork.contains(producerInfo1) ){
//					producerNetwork.add(producerInfo);
//				}
				
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
			if(lineitems.length > 3){
				MUtil.addOneByKey(rtitemsPerUser, uid);
			}
			
			if(user_times.containsKey(uid)){
				user_times.get(uid).add(time);
			}else{
				TreeSet<Long> times =new TreeSet<Long>();
				times.add(time);
				user_times.put(uid, times);
			}
			MUtil.addOneByKey(itemsPerUser, uid);

		}
		file.close();

		System.out.println("StartTime:"+new TimeUtil(start).getTime());
		System.out.println("EndTime:"+new TimeUtil(end).getTime());
		System.out.println("Email Num:"+num);
		for(String uid:user_times.keySet()){
			TreeSet<Long> times = user_times.get(uid);
			Long last=null;
			for(Long time:times){
				if(last==null){
					last=time;
				}else{
					long itemsInter =(long) Math.ceil(Double.valueOf(time -last)/ linkGranularity);
					MUtil.addOneByKey(itemsIntervals, itemsInter);
				}
			}
		}
		System.out.println("User Num:"+user_times.keySet().size());
//		Map<String,Integer> property= new HashMap<String,Integer>();
		
		for (String user : itemsPerUser.keySet()) {
			int postNum=itemsPerUser.get(user);
			double rtPost=0;
			if(rtitemsPerUser.containsKey(user)){
				rtPost=(double)rtitemsPerUser.get(user)/(double)postNum;
			}
			double postRate =  (double) postNum / (end - start);
			postRatePerUser.put(user,postRate);

//			MUtil.addOneByKey(property,rtPost+"\t"+postRate);
		}
		for (String dagId : pm.dags.keySet()) {
			DAG dag = pm.getDag(dagId);
			MUtil.addOneByKey(DAGHeight, dag.getHeight());
			MUtil.addOneByKey(DAGSize, dag.getMembers().size());
		}
		
		
		
//		MUtil.generateFile(producerNetwork, output + fileName + plus + "/", "producerNetwork.txt");
		MUtil.generateFile(MUtil.normal(DAGHeight), output + fileName + plus + "/",
				"DAGHeight.txt");
		MUtil.generateFile(MUtil.normal(DAGSize), output + fileName + plus + "/", "DAGSize.txt");
		MUtil.generateFile(MUtil.normal(week_Num), output+ fileName + plus + "/", "weekly.txt");
		MUtil.generateFile(MUtil.normal(day), output+ fileName + plus + "/", "day.txt");
		MUtil.generateFile(MUtil.normal(hour), output+ fileName + plus + "/", "hour.txt");
//		MUtil.generateFile(MUtil.normal(property), output+ fileName + plus + "/", "producerProporty.txt");

		MUtil.generateFile(MUtil.getNormalDis(itemIndegree), output + fileName + plus + "/",
				"itemIndegree.txt");
		MUtil.generateFile(MUtil.getRateNormalDis(postRatePerUser), output + fileName + plus + "/",
				"userPostRate.txt");

		TreeMap<Integer, Double> linkIntervalsDis = getIntervalsDis(linkIntervals, linkGranularity);
		MUtil.generateFile(linkIntervals, output + fileName + plus + "/", "linkIntervalsDis.txt");
		MUtil.generateFile(linkIntervalsDis, output + fileName + plus + "/", "linkIntervals.txt");
		MUtil.generateFile(MUtil.normal(itemsIntervals), output + fileName + plus + "/",
				"itemsIntervals.txt");
	}
	
	public void identifier(String path,String inputFileName, String outputFileName) throws IOException {
		Map<String, Integer> mmap = new HashMap<String, Integer>();
		int id = 1;

		BufferedReader file = new BufferedReader(new FileReader(path + inputFileName + ".txt"));
		BufferedWriter wfile = new BufferedWriter(new FileWriter(path + outputFileName + ".txt"));

		String line = null;
		while ((line = file.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			}

			String[] lineitems = line.split("\t");
			if(lineitems.length<2){
				continue;
			}
			String citing = lineitems[0];
			String cited = lineitems[1];
			if (citing != cited && !citing.equals(cited)) {
				if (!mmap.containsKey(citing)) {
					mmap.put(citing, id);
					id++;
				}
				if (!mmap.containsKey(cited)) {
					mmap.put(cited, id);
					id++;
				}
				wfile.write(mmap.get(citing) + "\t" + mmap.get(cited));
				wfile.newLine();
			}

		}
		file.close();
		wfile.flush();
		wfile.close();

		System.out.println("nodeNum=" + (id - 1));
	}

	public void getWeiboStreamingDistributions(String fileName) throws Exception {
		Map<String, Long> item_time = new HashMap<String, Long>();

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();
		TreeMap<String, Integer> itemsPerUser = new TreeMap<String, Integer>();
		TreeMap<Integer, Integer> linkIntervals = new TreeMap<Integer, Integer>();
		TreeMap<String, Double> userPostRate = new TreeMap<String, Double>();
//		TreeMap<String, Integer> itemsWithLinkPerUser = new TreeMap<String, Integer>();
		TreeMap<String, Double> rePostProPerUser = new TreeMap<String, Double>();
		TreeMap<Integer, Integer> itemsIntervals = new TreeMap<Integer, Integer>();
		Map<String, Long> user_lastTime = new HashMap<String, Long>();
		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> week = new TreeMap<Integer, Integer>();

		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String line = null;
		Long start = null, end = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		while ((line = file.readLine()) != null) {
			System.out.println(line);
			if (line.contains("#")) {
				continue;
			}
//			System.out.println(line);
			
			num++;
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];
			Long time = new TimeUtil(lineitems[1]).getSeconds();
//			Long time=Long.valueOf(lineitems[1]);
			String uid = lineitems[2];

			Calendar date = Calendar.getInstance();
//			date.setTime(new Date(Long.valueOf(lineitems[1])*1000));
			date.setTime(df.parse(lineitems[1]));
			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
			int hour_of_day = date.get(Calendar.HOUR_OF_DAY);
			int week_of_year = date.get(Calendar.WEEK_OF_YEAR);
			MUtil.addOneByKey(day, day_of_week);
			MUtil.addOneByKey(hour, hour_of_day);
			MUtil.addOneByKey(week, week_of_year);

			if (start == null) {
				start = time;
			}
			end = time;
			
//			if(end-start>(365*24*3600)){
//				break;
//			}
			
			item_time.put(citing, time);
			if (lineitems.length > 3) {
				String cited = lineitems[3];

				if (item_time.containsKey(cited)) {
					int linkInter = (int) Math.ceil(Double.valueOf(time - item_time.get(cited))
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}else{
					String rttime="";
					for(int j=0;j<cited.length();j++){
						if(Character.isDigit(cited.charAt(j))){
							rttime+=cited.charAt(j);
						}
					}
					int linkInter = (int) Math.ceil(Math.abs(time - Double.valueOf(rttime))
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
					
				}
				MUtil.addOneByKey(itemIndegree, cited);
//				MUtil.addOneByKey(itemsWithLinkPerUser, uid);
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

//			if (!itemsWithLinkPerUser.containsKey(uid)) {
//				itemsWithLinkPerUser.put(uid, 0);
//			}

			if (user_lastTime.containsKey(uid)) {
				int itemsInter = (int) Math.ceil(Double.valueOf(time - user_lastTime.get(uid))
						/ linkGranularity);

				MUtil.addOneByKey(itemsIntervals, itemsInter);
			}
			user_lastTime.put(uid, time);

			MUtil.addOneByKey(itemsPerUser, uid);

		}
		file.close();

		for (String user : itemsPerUser.keySet()) {
			userPostRate.put(user, (double) itemsPerUser.get(user) / (end - start));
		}
//		for (String user : itemsWithLinkPerUser.keySet()) {
//			rePostProPerUser.put(user, (double) itemsWithLinkPerUser.get(user) / itemsPerUser.get(
//					user));
//		}
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
		MUtil.generateFile(MUtil.normal(week), output + fileName + plus + "/", "weekly.txt");
	}

	public void getWeiboStreamingDistributions2(String fileName) throws Exception {
		Map<String, Long> item_time = new HashMap<String, Long>();

		Map<String, Integer> itemIndegree = new HashMap<String, Integer>();
		TreeMap<String, Integer> itemsPerUser = new TreeMap<String, Integer>();
		TreeMap<Integer, Integer> linkIntervals = new TreeMap<Integer, Integer>();
		TreeMap<String, Double> userPostRate = new TreeMap<String, Double>();
//		TreeMap<String, Integer> itemsWithLinkPerUser = new TreeMap<String, Integer>();
		TreeMap<String, Double> rePostProPerUser = new TreeMap<String, Double>();
		TreeMap<Integer, Integer> itemsIntervals = new TreeMap<Integer, Integer>();
		Map<String, Long> user_lastTime = new HashMap<String, Long>();
		TreeMap<Integer, Integer> day = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> hour = new TreeMap<Integer, Integer>();
		TreeMap<Integer, Integer> week = new TreeMap<Integer, Integer>();

		DAGManager pm = new DAGManager();

		Map<Integer, Integer> DAGHeight = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> DAGSize = new TreeMap<Integer, Integer>();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String line = null;
		Long start = null, end = null;
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		int num = 0;
		while ((line = file.readLine()) != null) {
			System.out.println(line);
			if (line.contains("#")) {
				continue;
			}
//			System.out.println(line);
			
			num++;
			String[] lineitems = line.split("\t");
			String citing = lineitems[0];
//			Long time = TimeUtil.changeWeiboTimeToSeconds(lineitems[1]);
			Long time=Long.valueOf(lineitems[1]);
			String uid = lineitems[2];

			Calendar date = Calendar.getInstance();
			date.setTime(new Date(Long.valueOf(lineitems[1])*1000));
//			date.setTime(df.parse(lineitems[1]));
			int day_of_week = date.get(Calendar.DAY_OF_WEEK);
			int hour_of_day = date.get(Calendar.HOUR_OF_DAY);
			int week_of_year = date.get(Calendar.WEEK_OF_YEAR);
			MUtil.addOneByKey(day, day_of_week);
			MUtil.addOneByKey(hour, hour_of_day);
			MUtil.addOneByKey(week, week_of_year);

			if (start == null) {
				start = time;
			}
			end = time;
			
//			if(end-start>(365*24*3600)){
//				break;
//			}
			
			item_time.put(citing, time);
			if (lineitems.length > 3) {
				String cited = lineitems[3];

				if (item_time.containsKey(cited)) {
					int linkInter = (int) Math.ceil(Double.valueOf(time - item_time.get(cited))
							/ linkGranularity);
					MUtil.addOneByKey(linkIntervals, linkInter);
				}
				MUtil.addOneByKey(itemIndegree, cited);
//				MUtil.addOneByKey(itemsWithLinkPerUser, uid);
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

//			if (!itemsWithLinkPerUser.containsKey(uid)) {
//				itemsWithLinkPerUser.put(uid, 0);
//			}

			if (user_lastTime.containsKey(uid)) {
				int itemsInter = (int) Math.ceil(Double.valueOf(time - user_lastTime.get(uid))
						/ linkGranularity);

				MUtil.addOneByKey(itemsIntervals, itemsInter);
			}
			user_lastTime.put(uid, time);

			MUtil.addOneByKey(itemsPerUser, uid);

		}
		file.close();

		for (String user : itemsPerUser.keySet()) {
			userPostRate.put(user, (double) itemsPerUser.get(user) / (end - start));
		}
//		for (String user : itemsWithLinkPerUser.keySet()) {
//			rePostProPerUser.put(user, (double) itemsWithLinkPerUser.get(user) / itemsPerUser.get(
//					user));
//		}
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
		MUtil.generateFile(MUtil.normal(week), output + fileName + plus + "/", "weekly.txt");
	}
	
	public TreeMap<Integer, Double> getIntervalsDis(Map<Long, Integer> linkIntervals,
			long linkGranularity) throws IOException {
		TreeMap<Integer, Integer> result = new TreeMap<Integer, Integer>();
		for (Long time : linkIntervals.keySet()) {
			int key = (int) Math.ceil(Double.valueOf(time) / linkGranularity);
//			int key = (int) (mutil*linkGranularity);
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

		System.out.println("start Time:" + new TimeUtil(startTime).getTime()
				+ " end time:" + new TimeUtil(endTime).getTime());
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
	
	public void getProducerProporty(String filePath, String outFile) throws IOException{
		BufferedReader file = new BufferedReader(new FileReader(filePath));
		Map<String,Integer> proportyDis = new HashMap<String,Integer>();
		Map<String,String> item_user = new HashMap<String,String>();
		Map<String,Integer> user_itemNum = new HashMap<String,Integer>();
		Map<String,Integer> user_itemWithLinkNum = new HashMap<String,Integer>();
		Set<String> producerNetwork= new HashSet<String>();
		String line = null;
		line=file.readLine();
		Integer start=null,end=null;
		while ( line!= null) {
			String[] lineitems = line.split("\t");
			String user = lineitems[2];
			Integer time = Integer.valueOf(lineitems[1]);
			if(start==null){
				start=end=time;
			}else{
				if(start>time){
					start=time;
				}
				if(end<time){
					end=time;
				}
			}
			item_user.put(lineitems[0], user);
			if(lineitems.length>3){
				MUtil.addOneByKey(user_itemWithLinkNum, user);
				String rtmid = lineitems[3];
				String rtuid="";
				for(int j=0;j<rtmid.length();j++){
					if(!Character.isDigit(rtmid.charAt(j))){
						rtuid+=rtmid.charAt(j);
					}else{
						break;
					}
				}
				String producerInfo= user+"\t"+rtuid;
				String producerInfo1= rtuid+"\t"+user;
				if(!producerNetwork.contains(producerInfo) && !producerNetwork.contains(producerInfo1) ){
					producerNetwork.add(producerInfo);
				}
			}
			MUtil.addOneByKey(user_itemNum, user);
			
			
			line=file.readLine();
		}
		

		file.close();
		
		
		
		for(String user:user_itemNum.keySet()){
			double rate =(double) user_itemNum.get(user)/(end-start);
			double p=0;
			if(user_itemWithLinkNum.containsKey(user)){
				p=(double)user_itemWithLinkNum.get(user)/user_itemNum.get(user);
			}
			
			String proporty= p+"\t"+rate;
			MUtil.addOneByKey(proportyDis, proporty);
		}
		MUtil.generateFile(producerNetwork, outFile, "producerNetwork.txt");
		
		MUtil.generateFile(MUtil.normal(proportyDis), outFile, "producerProporty.txt");
	}
	public void getDegreeDistribution(String fileName) throws IOException {
		BufferedReader file = new BufferedReader(new FileReader(input + fileName + ".txt"));
		Map<String, Integer> degree = new TreeMap<String, Integer>();

		Set<String> edges = new HashSet<String>();
		Set<String> nodes = new HashSet<String>();
		String line = null;
		int count = 0;
		while ((line = file.readLine()) != null) {
			if (line.contains("\"") || line.contains("#")) {
				continue;
			}
			String[] lineitems = line.split("\t");
			String other = lineitems[1] + " " + lineitems[0];
			if (!edges.contains(line) && !edges.contains(other)) {
				MUtil.addOneByKey(degree, lineitems[0]);
				MUtil.addOneByKey(degree, lineitems[1]);
				count++;
			}
			nodes.add(lineitems[1]);
			nodes.add(lineitems[0]);
		}
		file.close();
		MUtil.generateFile(MUtil.getNormalDis(degree), output, "Deg_" + fileName + ".txt");
//		System.out.println("edges num:" + count);
		System.out.println("nodes num:" + nodes.size());
	}
	public static void  main(String[] args) throws Exception{
		String input_gen = "/Users/ycc/kuaipan/workspace/SocialStreamGen/data/GenProducerNetwork/email/";
//		String input_gen = "/Users/ycc/kuaipan/data/gnuplot_sourcedata/email/";
		String output_gen = input_gen+ "Distribution/";
		 Measurements mm = new Measurements(input_gen, output_gen,"weibo");
//		 mm.getProducerProporty(input_gen+"emailStream.txt", input_gen);
//		 mm.paixu("emailStream");
//		 mm.getDistributions("emailStream",true);
//		 mm.Actibities("emailStream");
//		 mm.DeleteDuplicate("producerNetwork_weibo");
//		 mm.identifier(input_gen, "producerNetwork", "producerNetwork1");
		 mm.getDegreeDistribution("email_20000");
	}

}
