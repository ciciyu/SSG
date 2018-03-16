package Measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Chengcheng Yu
 */
public class MUtil {

	public static <E, V> void addKeyValue(Map<E, Set<V>> it, E key, V value) {
		if (it.containsKey(key)) {
			it.get(key).add(value);
		} else {
			Set<V> s = new HashSet<V>();
			s.add(value);
			it.put(key, s);
		}
	}

	public static <E> void addOneByKey(Map<E, Integer> it, E key) {
		if (it.containsKey(key)) {
			it.put(key, it.get(key) + 1);
		} else {
			it.put(key, 1);
		}
	}

	public static <E> void addZeroByKey(Map<E, Integer> it, E key) {
		if (!it.containsKey(key)) {
			it.put(key, 0);
		}
	}

	public static <E> void setKeyValue(Map<E, Integer> it, E key, Integer value) {
		it.put(key, value);
	}

	public static <E> Map<Integer, Double> getNormalDis(Map<E, Integer> it) {
		Map<Integer, Integer> distribution = new TreeMap<Integer, Integer>();
		Map<Integer, Double> result = new TreeMap<Integer, Double>();

		for (E key : it.keySet()) {
			addOneByKey(distribution, it.get(key));
		}
		for (Integer key : distribution.keySet()) {
			result.put(key, (double) distribution.get(key) / it.size() * 100);
		}
		return result;
	}

	public static <E> Map<Double, Double> getRateNormalDis(Map<E, Double> it) {
		Map<Double, Integer> distribution = new TreeMap<Double, Integer>();
		Map<Double, Double> result = new TreeMap<Double, Double>();

		for (E key : it.keySet()) {
			addOneByKey(distribution, it.get(key));
		}
		for (Double key : distribution.keySet()) {
			result.put(key, (double) distribution.get(key) / it.size() * 100);
		}
		return result;
	}

	public static <E, V> void generateFile(Map<E, V> it, String output, String fileName)
			throws IOException {

		File f = new File(output);
		if (!f.exists() && !f.isDirectory()) {
			f.mkdir();
		}

		String path = output + "/" + fileName;
		BufferedWriter file = new BufferedWriter(new FileWriter(path));
		for (E key : it.keySet()) {
			file.write(key + "\t" + it.get(key));
			file.newLine();
		}
		file.flush();
		file.close();
	}

	public static <E> void generateFile(Set<E> it, String output, String fileName)
			throws IOException {

		File f = new File(output);
		if (!f.exists() && !f.isDirectory()) {
			f.mkdir();
		}

		String path = output + "/" + fileName;
		BufferedWriter file = new BufferedWriter(new FileWriter(path));
		for (E key : it) {
			file.write(key + "");
			file.newLine();
		}
		file.flush();
		file.close();
	}

	public static <E> TreeMap<E, Double> normal(Map<E, Integer> it) {
		double sum = 0;
		for (E key : it.keySet()) {
			sum += it.get(key);
		}
		TreeMap<E, Double> result = new TreeMap<E, Double>();
		for (E key : it.keySet()) {
			result.put(key, (it.get(key) / sum) * 100);
		}
		return result;
	}

	public static Map<Integer, Double> getIntervalsDis(Map<Long, Integer> linkIntervals,
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



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
