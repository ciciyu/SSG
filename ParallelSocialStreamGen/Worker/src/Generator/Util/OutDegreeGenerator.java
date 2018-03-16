package Generator.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

import Common.Generator.DiscreteGenerator;

/**
 * Generates a distribution by choosing from a discrete set of values.
 */
public class OutDegreeGenerator extends DiscreteGenerator<Integer> {

	TreeMap<Integer, Double> identifier = new TreeMap<Integer, Double>();

	public OutDegreeGenerator(String discretePath) throws IOException {
		File file = new File(discretePath);
		if (file.exists()) {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Integer value = Integer.valueOf(linesItem[0]);
				Double weight = Double.valueOf(linesItem[1]);
				addValue(weight, value);
			}
			in.close();
		}
	}

	public void addValue(Double weight, Integer value) {
		super.addValue(weight, value);
		identifier.put(value, _items.lastKey());
	}

	public Integer nextValue(Integer vaxValue) {

		Entry<Integer, Double> max = identifier.ceilingEntry(vaxValue);
		if (max == null) {
			max = identifier.floorEntry(vaxValue);
		}

		double val = getRandom().nextDouble() * max.getValue();

		Entry<Double, Integer> entry = _items.ceilingEntry(val);
		if (entry != null) {
			_last = entry.getValue();
		}
		return _last;
	}

}
