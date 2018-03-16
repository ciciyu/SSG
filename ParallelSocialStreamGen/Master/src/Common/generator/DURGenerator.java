package Common.generator;

import java.util.TreeMap;

/**
 * An expression that generates a sequence of string values, following some distribution (Uniform, Zipfian, Sequential, etc.)
 */
public abstract class DURGenerator<E> extends Generator<E>{
	TreeMap<Double,E> _items = new TreeMap<Double,E>();
	
	public E nextValue() {
		// TODO Auto-generated method stub
		if(!_items.isEmpty()){
			Double key = _items.firstKey();
			_last=_items.remove(key);
			return _last;
		}else{
			return null;
		}
		
		
	}

	public E lastValue() {
		// TODO Auto-generated method stub
		return _last;
	}
	
	
	public void clear() {
		// TODO Auto-generated method stub
		_items.clear();
	}
	
	
}


