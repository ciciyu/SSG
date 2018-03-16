package Common.generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Generates a value by choosing from a discrete set of values. Each value may
 * be chosen once at most.
 */
public class DiscreteGenerator<E> extends Generator<E> {
	protected TreeMap<Double, E> _items;// <weight,value>
	boolean _isUnique;
	
	public DiscreteGenerator(boolean isUnique) {
		_items = new TreeMap<Double, E>();
		_isUnique=isUnique;
	}
	public DiscreteGenerator() {
		_items = new TreeMap<Double, E>();
		_isUnique=false;
	}
	public DiscreteGenerator(DiscreteGenerator<E> gen,boolean isUnique){
		_items= new TreeMap<Double, E>(gen.getItems());
		_isUnique=isUnique;
	}
	

	public void addValue(Double weight, E value) {
		Double key = null;
		if (_items.isEmpty()) {
			key = weight;
			_items.put(key, value);
		} else {
			key = _items.lastKey() + weight;
			_items.put(key, value);
		}
	}

	/**
	 * Generate the next value in the distribution.
	 */

	public E nextValue() {
		// TODO Auto-generated method stub
		if (!isEmpty()) {
			double val = getRandom().nextDouble() * _items.lastKey();
			Entry<Double, E> item = _items.ceilingEntry(val);

			_last = item.getValue();
			if(_isUnique){
				_items.remove(item.getKey());
			}
			
			return _last;

		} else {
			return null;
		}
	}

	public void clear() {
		_items.clear();
		_last = null;
		ran = new Random();
	}

	public boolean isEmpty() {
		if (_items == null || _items.isEmpty())
			return true;
		else
			return false;
	}

	public Integer getSize() {
		return _items.size();
	}

	public E lastValue() {
		// TODO Auto-generated method stub
		return _last;
	}
	
	public TreeMap<Double, E> getItems(){
		return _items;
	}

	public boolean is_isUnique() {
		return _isUnique;
	}
	public void set_isUnique(boolean _isUnique) {
		this._isUnique = _isUnique;
	}
	
	public Collection<E> getAllValue(){
		return _items.values();
	}
}
