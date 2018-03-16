package Object;

import java.util.Comparator;

public class ComparatorItem implements Comparator<Item>{

	public int compare(Item m1, Item m2) {
		// TODO Auto-generated method stub
		
		if(m1.getTime().equals(m2.getTime())){
			return m1.getId().compareTo(m2.getId());
		}else{
			return m1.getTime().compareTo(m2.getTime());
		}
	}
	
}