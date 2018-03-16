package Generator.Util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import Common.generator.DiscreteGenerator;

/**
 * Generates a value by choosing from a discrete set of values. Each value may
 * be chosen once at most.
 */
public class LinkFriendGenerator<E> extends DiscreteGenerator<E> {
	public LinkFriendGenerator(DiscreteGenerator<E> gen) {
		_items = new TreeMap<Double,E>(gen.getItems());
		
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
			_items.remove(item);
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

}
