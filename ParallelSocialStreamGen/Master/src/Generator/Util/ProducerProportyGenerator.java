package Generator.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Common.generator.DiscreteGenerator;

/**
 * Generates a distribution by choosing from a discrete set of values.
 */
public class ProducerProportyGenerator extends DiscreteGenerator<String> {

	public ProducerProportyGenerator(String discretePath) throws IOException {
		File file = new File(discretePath);
		if (file.exists()) {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("#")) {
					continue;
				}
				String[] linesItem = line.split("\t");
				Double weight = Double.valueOf(linesItem[linesItem.length-1]);
				String value = "";
				for (int i = 0; i < linesItem.length-1; i++) {
					if (i == (linesItem.length - 2)) {
						value += (linesItem[i]);
					} else {
						value += (linesItem[i] + "\t");
					}
				}
				addValue(weight, value);
			}
			in.close();
		}
	}


}
