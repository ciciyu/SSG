package Generator.Util;

import java.io.IOException;

import Common.Generator.CounterGenerator;

/**
 * @author Chengcheng Yu
 */
public class GeneratorFactory extends AbstractGenerator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see Common.generator.AbstractGeneratorFactory#createCounterGenerator()
	 */
	@Override
	public CounterGenerator createItemIdentifierGenerator() {
		// TODO Auto-generated method stub
		return new CounterGenerator(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Common.generator.AbstractGeneratorFactory#createRateGenerator()
	 */
	@Override
	public ProducerProportyGenerator createRateGenerator(String filePath) throws IOException {
		// TODO Auto-generated method stub
		return new ProducerProportyGenerator(filePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Common.generator.AbstractGeneratorFactory#createOutDegreeGenerator()
	 */
	@Override
	public OutDegreeGenerator createOutDegreeGenerator(String filePath) throws IOException {
		// TODO Auto-generated method stub
		return new OutDegreeGenerator(filePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Common.generator.AbstractGeneratorFactory#createLinkIntervalsGenerator()
	 */
	@Override
	public LinkIntervalsGenerator createLinkIntervalsGenerator(String filePath)
			throws IOException {
		// TODO Auto-generated method stub
		return new LinkIntervalsGenerator(filePath);
	}

	

}
