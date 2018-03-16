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
public class LinkIntervalsGenerator extends DiscreteGenerator<Long> {

	Long linkGranularity;
	TreeMap<Long, Double> identifier = new TreeMap<Long, Double>();

	public LinkIntervalsGenerator(String discretePath) throws IOException {
		File file = new File(discretePath);

		if (file.exists()) {
			boolean isfirstLine = true;
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Long value = Long.valueOf(linesItem[0]);
				Double weight = Double.valueOf(linesItem[1]);
				addValue(weight, value);
				if (isfirstLine && value != 0) {
					linkGranularity = value;
					isfirstLine = false;
				}
			}
			in.close();
		}
	}

	public void addValue(Double weight, Long value) {
		super.addValue(weight, value);
		identifier.put(value, _items.lastKey());
	}

	public Long getLinkGranularity() {
		return linkGranularity;
	}

	public Long nextValue(Long vaxValue) {
		Entry<Long, Double> max = identifier.ceilingEntry(vaxValue);
		if (max == null) {
			max = identifier.floorEntry(vaxValue);
		}
		double val = getRandom().nextDouble() * max.getValue() ;

		Entry<Double, Long> entry = _items.ceilingEntry(val);
		if (entry != null) {
			_last = entry.getValue();
		}
		return _last;
	}

}
