package Generator.Util;

import java.util.Comparator;

import Object.Item;

/** 
 * @ClassName: ComparatorByTimeReverseOrder 
 * @Description: TODO
 * @author Chengcheng Yu
 *  
 */
public class ComparatorByTimeOrder implements Comparator<Item> {

	
	public int compare(Item t1, Item t2) {
		// TODO Auto-generated method stub
		if(t1.getTime().equals(t2.getTime())){
			if(t1.getId()==null){
				return t1.getUid().compareTo(t2.getUid());
			}else{
				return t1.getId().compareTo(t2.getId());
			}
			
		}else{
			return t1.getTime().compareTo(t2.getTime());
		}
		
	}

}