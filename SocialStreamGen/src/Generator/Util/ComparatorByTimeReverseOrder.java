package Generator.Util;

import java.util.Comparator;

import Object.Item;

/** 
 * @ClassName: ComparatorByTimeReverseOrder 
 * @Description: TODO
 * @author Chengcheng Yu
 *  
 */
public class ComparatorByTimeReverseOrder implements Comparator<Item> {

	
	public int compare(Item t1, Item t2) {
		// TODO Auto-generated method stub
		long comp = t1.getTime() - t2.getTime();
		if (comp == 0) {
			return 0;
		} else {
			return comp > 0 ? 1 : -1;
		}
//		if (t1.getTime() > t2.getTime())
//			return -1;
//		else if (t1.getTime() < t2.getTime())
//			return 1;
//		else {
//			Integer uid1 = Integer.valueOf(t1.getUid());
//			Integer uid2 = Integer.valueOf(t2.getUid());
//			return uid2 - uid1;
//		}
	}

}