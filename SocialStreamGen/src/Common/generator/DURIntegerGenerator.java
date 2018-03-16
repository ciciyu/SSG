package Common.generator;

import java.util.TreeMap;

/** 
 * Generates a sequence of integers with following properties (DUR):
 * Dense: All the integers in [0..N] appear in the sequence.
 * Unique: Each integer appears exactly once.
 * Random: The sequence appears to be "random" (is pseudo-random).
 */
public class DURIntegerGenerator extends DURGenerator<Integer>{

	Integer _maxInt;
	TreeMap<Double,Integer> _sort = new TreeMap<Double,Integer>();
//	Integer _last;
	
	public DURIntegerGenerator(Integer maxInt) {
		// TODO Auto-generated constructor stub
		_maxInt = maxInt;
		for(int i=0;i<maxInt+1;i++){
			_sort.put(getRandom().nextDouble(), i);
		}
	}

	/* (non-Javadoc)
	 * @see Common.generator.IntegerGenerator#nextInt()
	 */
	@Override
	public Integer nextValue() {
		// TODO Auto-generated method stub
		if(!_sort.isEmpty()){
			Double key = _sort.firstKey();
			_last = _sort.remove(key);
		}else{
			_last = null;
		}
		return _last;
	}

	/* (non-Javadoc)
	 * @see Common.generator.Generator#lastValue()
	 */
	@Override
	public Integer lastValue() {
		// TODO Auto-generated method stub
		return _last;
	}

	
}
