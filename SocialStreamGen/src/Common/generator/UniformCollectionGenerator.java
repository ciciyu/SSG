package Common.generator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** 
 * @author Chengcheng Yu
 */
public class UniformCollectionGenerator<E> extends Generator<E>{

	public Collection<E> _items= new HashSet<E>();
	public UniformIntegerGenerator _gen = new UniformIntegerGenerator(0,0);
	
	public UniformCollectionGenerator(Collection<E> items){
		_items.addAll(items);
		_gen.set_lb(0);
		_gen.set_ub(items.size()-1);
	}
	
	public UniformCollectionGenerator(){
	}
	
	public void addValues(Set<E> items){
		_items.addAll(items);
		_gen.set_lb(0);
		_gen.set_ub(_items.size()-1);
	}
	
	public E nextValue()
	{
		if(_items.isEmpty()){
			return null;
		}else{
			Object[] ls = _items.toArray();
			_last = (E) ls[_gen.nextValue()];
			return _last;
		}
		
	}
	
	public E lastValue(){
		return _last;
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		_items.clear();
		_last=null;
	}

	public boolean isEmpty(){
		if(!_items.isEmpty() && _items!=null){
			return false;
		}else{
			return true;
		}
	}


}
