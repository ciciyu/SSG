package Generator.Util;

import java.io.IOException;

import Common.generator.CounterGenerator;

/**
 * @author Chengcheng Yu
 */
public abstract class AbstractGenerator {
	/**
	 * Create item identifier Generator
	 */
	public abstract CounterGenerator createItemIdentifierGenerator();

	/**
	 * Create user post rate Generator
	 */
	public abstract ProducerProportyGenerator createRateGenerator(String filePath) throws IOException;

	/**
	 * Create item outDegree Generator
	 */
	public abstract OutDegreeGenerator createOutDegreeGenerator(String filePath)
			throws IOException;

	/**
	 * Create link intervals Generator
	 */
	public abstract LinkIntervalsGenerator createLinkIntervalsGenerator(String filePath)
			throws IOException;


}
