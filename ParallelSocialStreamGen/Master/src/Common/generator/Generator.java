package Common.generator;

import java.util.Random;

/**
 * An expression that generates a sequence of string values, following some distribution (Uniform, Zipfian, Sequential, etc.)
 */
public abstract class Generator<E> {
	
	public E _last;
	public Random ran =new Random();
	
	/**
	 * Generate the next value in the distribution.
	 * @param n 
	 */
	
	public abstract E nextValue();
	public abstract E lastValue();
	
	public Random getRandom(){
		return ran;
	}
}


