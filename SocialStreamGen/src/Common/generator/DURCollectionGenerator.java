package Common.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generates a sequence of strings from the specified set with following
 * properties: Unique: Each item appears exactly once. Random: The sequence
 * appears to be "random" (is pseudo-random).
 */
public class DURCollectionGenerator<E> extends DURGenerator<E> {

//	List<E> items= new ArrayList<E>();

	public DURCollectionGenerator() {

	}

	public DURCollectionGenerator(Collection<E> items) {
		// TODO Auto-generated constructor stub
		
		for (E it : items) {
			_items.put(getRandom().nextDouble(), it);
		}
	}

	public void addValue(E value) {
		_items.put(getRandom().nextDouble(), value);
	}
	
	public void addValue(Collection<E> items){
		for (E it : items) {
			_items.put(getRandom().nextDouble(), it);
		}
	}

	public E nextValue() {
		// TODO Auto-generated method stub
		if (!_items.isEmpty()) {
			Double key = _items.firstKey();
			_last = _items.remove(key);
			return _last;
		} else {
			return null;
		}

	}

	public void clean(){
		_items.clear();
	}
}
