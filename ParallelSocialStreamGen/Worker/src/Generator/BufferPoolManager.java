package Generator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import Generator.Util.ComparatorByTimeOrder;
import Object.Item;

public class BufferPoolManager {

	private TreeSet<Item> nextPool; // <uid,time>
	private Map<String, TreeMap<Long, Set<String>>> recentPool;// <uid,<time,ItemIDs>>
	private TreeSet<Item> firstPool;
//	public Map<String,String> item_root;

	public BufferPoolManager() {
		nextPool = new TreeSet<Item>(new ComparatorByTimeOrder());
		recentPool = new HashMap<String, TreeMap<Long, Set<String>>>
		((int) (Parameter.users.keySet().size()/0.75)+1);
		firstPool = new TreeSet<Item>(new ComparatorByTimeOrder());
//		item_root= new HashMap<String,String>();
	}
	public Long getFirstTime() {
		if (firstPool.isEmpty()) {
			return Parameter.workerInfo.getStartTime();
		} else {
			return firstPool.first().getTime()-1;
		}
	}

	public void addItemToNextPool(Item item) {
		nextPool.add(item);
	}

	public void putToRecentPool(Item item) {
		if (recentPool.containsKey(item.getUid())) {
			if(recentPool.get(item.getUid()).containsKey(item.getTime())){
				recentPool.get(item.getUid()).get(item.getTime()).add(item.getId());
			}else{
				Set<String> items = new HashSet<String>();
				items.add(item.getId());
				recentPool.get(item.getUid()).put(item.getTime(), items);
			}
		} else {
			Set<String> items = new HashSet<String>();
			items.add(item.getId());
			TreeMap<Long, Set<String>> ts = new TreeMap<Long, Set<String>>();
			ts.put(item.getTime(), items);
			recentPool.put(item.getUid(), ts);
			firstPool.add(item);
		}
		if (!firstPool.isEmpty()) {
			long firstTime = firstPool.first().getTime();
			long gap = item.getTime() - firstTime;
			while (gap > Parameter.workerInfo.getWindowSize() && !firstPool.isEmpty()) {
				Item first = firstPool.pollFirst();
				recentPool.get(first.getUid()).get(first.getTime()).remove(first.getId());
				if (recentPool.get(first.getUid()).get(first.getTime()).isEmpty()) {
					recentPool.get(first.getUid()).remove(first.getTime());
				}
				if (recentPool.get(first.getUid()).isEmpty()) {
					recentPool.remove(first.getUid());
				}
				if (!recentPool.isEmpty() && recentPool.containsKey(first.getUid())) {
					Entry<Long, Set<String>> next = recentPool.get(first.getUid()).firstEntry();

					if (next != null) {
						String nextID = (String) next.getValue().toArray()[0];
						Item nextItem = new Item(nextID, next.getKey(), first.getUid());
						firstPool.add(nextItem);
					}
				}

				if (!firstPool.isEmpty()) {
					firstTime = firstPool.first().getTime();
					gap = item.getTime() - firstTime;
				}
			}
		}
		
		
	}

	public Item removeFirstItemInNextPool() {
		return this.nextPool.pollFirst();
	}


	public Map<String, TreeMap<Long, Set<String>>> getRecentPool() {
		return recentPool;
	}


	public TreeSet<Item> getNextPool() {
		return nextPool;
	}
	
	public Long getFirstTimeFromNextPool(){
		if(!nextPool.isEmpty()){
			return nextPool.first().getTime();
		}else{
			return null;
		}
	}

}
