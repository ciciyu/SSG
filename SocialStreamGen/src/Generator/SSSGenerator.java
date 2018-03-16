package Generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import Generator.Util.TimeUtil;
import Object.Item;

/**
 * @author Chengcheng Yu
 * 
 */
public class SSSGenerator {

	private AbstractSSSGenTool genTool;

	private BufferedWriter file;
	private BufferedWriter logfile;
	 private String path;

	//private Long itime;
	public SSSGenerator(String path) throws Exception {

		System.out.println("parameter start...");
		this.path=path;
		genTool = SSSGenToolProvider.createSSSGenTool(path);

		createOutFile();
		//itime=Parameter.startTime;
		System.out.println("parameter end...");
	}

	/**
	 * Generate social stream.
	 */
	public void generateSocialStream() throws Exception {
		initNextPool();
		updatePool();
	}

	/**
	 * Get the first item of each producer for initialization nextPool.
	 * 
	 * @throws IOException
	 */
	private void initNextPool() throws ParseException, IOException {
		System.out.println("initPool start...");
		long start = Parameter.startTime;
		for (Integer uid : Parameter.users.keySet()) {
			Long time = genTool.nextTime(start, uid);
			if (time != null) {
				Parameter.pool.addItemToNextPool(new Item(time, uid));
			}
		}
		System.out.println("initPool end...");
	Statistic.baseMemory=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
	}

	/**
	 * Update these two pools (nextPool and recentPool) over time for generating
	 * social streams.
	 */
	private void updatePool() throws Exception {
		System.out.println("updatePool start...");

		while (!Parameter.pool.getNextPool().isEmpty()) {
			/* remove the first item in nextPool */
			Item item = Parameter.pool.removeFirstItemInNextPool();

			/* get the next item without link set, item.uid publish */
			Long nextTime = genTool.nextTime(item);
			if (nextTime != null) {
				/* update the next item to nextPool */
				Parameter.pool.addItemToNextPool(new Item(nextTime, item.getUid()));
			}

			/* set the item's identifier */
			item.setId(Parameter.IDGen.nextValue());
			/* set the item's link set */
			Set<Integer> links = genTool.getLinks(item);
			/* put the item to recent Pool */
			Parameter.pool.putToRecentPool(item);

			outputSocialStream(item, links);
		}

		closeAllFile();
		System.out.println("updatePool end...");
		Statistic.outPutStatistic();
	}
	


	/**
	 * create out file
	 */
	private void createOutFile() throws Exception {
		File outFile = new File(path + "/out");
		if (!outFile.exists() && !outFile.isDirectory()) {
			outFile.mkdir();
		}
		String start = new TimeUtil(Parameter.dataType).changeTimeToString(Parameter.startTime);
		String end = new TimeUtil(Parameter.dataType).changeTimeToString(Parameter.endTime);
		file = new BufferedWriter(new FileWriter(outFile + "/" + Parameter.dataType + "_"
				+ Parameter.userNum + "_"+ start+"-"+end+".txt"));
//		logfile = new BufferedWriter(new FileWriter(outFile + "/" + Parameter.dataType + "_"
//				+ Parameter.userNum + "_log.txt"));
		// file_linkNetwork = new BufferedWriter(new FileWriter(outFile + "/" +
		// genTool.getPara()
		// .getDataType() + "_" + genTool.getPara().getIteration() +
		// "_linkNetwork.txt"));
	}

	/**
	 * Output each item to file in disk
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	private void writeLog() throws IOException{
		double runningTime = (System.currentTimeMillis() - Parameter.runningStart) / 1000f;

		logfile.write("itemNum: " + Parameter.IDGen.lastValue());
		logfile.newLine();
		logfile.write("linkNum: " + Statistic.linkNum);
		logfile.newLine();
		logfile.write("running time: " + runningTime);
		logfile.newLine();
		logfile.write("Throught(item) : " + (double) Parameter.IDGen.lastValue() / runningTime
				+ " items/seconds ");
		logfile.newLine();
//		logfile.write("UsedMemory : " + Statistic.UsedMemory*9.5367e-7+"Mb" );
//		logfile.newLine();
		logfile.flush();
		logfile.close();
		
	}
	
	private void outputSocialStream(Item item, Set<Integer> links) throws IOException {
		// TODO Auto-generated method stub
		// outputLinkNetwork(item, links);
		String result = item.toString(Parameter.dataType);
		if (links != null) {
			for (Integer key : links) {
				result += ("\t" + key);
			}
		}
		System.out.println(result);
		file.write(result);
		file.newLine();
		viewOutput(item,links);
	}

	private int identifier=0;
	int month=1;
	boolean flag=false;
	private void viewOutput(Item item,Set<Integer> links) {
		// TODO Auto-generated method stub
		Date d = new Date(item.getTime()*1000);
		int it=0;
		if(Parameter.viewSize.equals("day")){
			it = d.getDay();
		}else if(Parameter.viewSize.equals("month")){
			it = d.getMonth();
			
		}else if(Parameter.viewSize.equals("year")){
			it = d.getYear();
		}
		Statistic.setUsedMemory(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed());
		
		if(identifier != it){
			identifier= it;
			System.out.print(item.toString(Parameter.dataType));
			System.out.println("\t"+links.toString());
			month++;
			flag=true;
		}

	}
	/**
	 * Flush and close file
	 * 
	 * @throws IOException
	 **/
	private void closeAllFile() throws IOException {

		// TODO Auto-generated method stub
		file.flush();
		file.close();

		// System.out.println("Goal UserNume = " + Math.pow(2,
		// genTool.getPara().getIteration()));
		System.out.println("Real UserNume = " + Parameter.users.size());
	}

}