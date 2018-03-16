package Common.Generator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates a sequence of integers 0, 1, ...
 */
public class CounterGenerator extends Generator<Integer>{

	final AtomicInteger counter;
	/**
	 * Create a counter that starts at countstart
	 */
	public CounterGenerator(int countstart)
	{
		counter=new AtomicInteger(countstart);
	}

	/**
	 * If the generator returns numeric (integer) values, return the next value as an int. Default is to return -1, which
	 * is appropriate for generators that do not return numeric values.
	 */
	public Integer nextValue() 
	{
		_last = counter.getAndIncrement();
		return 	_last;
	}

	public Integer lastValue() {
		// TODO Auto-generated method stub
		return _last;
	}


	

}
