package Generator.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import Common.generator.ZipfianGenerator;

/** 
 * @author Chengcheng Yu
 */
public class RateGenerator {

	double aLow=0.000325;
	double bLow = -0.01;
	double cLow=1;

	double aUp=0.0002023;
	double bUp = 3.5;
	double cUp=206;
	long range = 0;
	Map<Integer,ZipfianGenerator > powerLawGen = new HashMap<Integer,ZipfianGenerator >();
	
//	DiscreteGenerator<String> exponentGen = new DiscreteGenerator<String>();
	TreeMap<Integer,String> bound= new TreeMap<Integer,String>();
	public RateGenerator(String discretePath) throws Exception{
		TimeUtil util =new TimeUtil("patent");
		range =util.changeTimeToSeconds("1999-12-28") - util
				.changeTimeToSeconds("1969-01-07");
		File file = new File(discretePath);
		if (file.exists()) {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Integer degree = Integer.valueOf(linesItem[0]);
				String value = linesItem[1]+"\t"+linesItem[2];
				bound.put(degree, value);
			}
			in.close();
		}
	}
	public double getNextRate(int degree){
		
		if(!powerLawGen.containsKey(degree)){
			String[] bounds = getBound(degree).split("\t");
			int low = Integer.valueOf(bounds[0]);
			int up = Integer.valueOf(bounds[1])+10;
			powerLawGen.put(degree, new ZipfianGenerator(low,up,1.89,1.89));
		}
		
		int postNum=powerLawGen.get(degree).nextInt();
		return (double)postNum/range;
	}
	
	public String getBound(int degree){
		Entry<Integer, String> entry= bound.floorEntry(degree);
		if(entry==null){
			entry=bound.ceilingEntry(degree);
		}
		return entry.getValue();
	}
	
	public void clear(){
		bound.clear();
		powerLawGen.clear();
	}
}
