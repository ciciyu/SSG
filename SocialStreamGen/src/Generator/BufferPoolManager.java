package Generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import Generator.Util.ComparatorByTimeOrder;
import Object.Item;
import Object.Neighbor;

public class BufferPoolManager {

	private TreeSet<Item> nextPool; // <uid,time>

	private Map<Integer, TreeMap<Long, Set<Integer>>> recentPool;// <uid,<time,ItemIDs>>

	private TreeSet<Item> firstPool;

	private Map<Integer,Integer> itemIndegree;
	
	
	public Map<Integer,Set<Integer>> links;

	public BufferPoolManager() {
		nextPool = new TreeSet<Item>(new ComparatorByTimeOrder());
		if(!Parameter.dataType.equals("patent")){
			recentPool = new HashMap<Integer, TreeMap<Long, Set<Integer>>>();
			firstPool = new TreeSet<Item>(new ComparatorByTimeOrder());
			itemIndegree = new HashMap<Integer,Integer>();
		}else{
			links = new HashMap<Integer,Set<Integer>>();
		}
	}
	
	
	public  Integer getItemIndegree(Integer itemID){
		int weight =1;
		if(itemIndegree.containsKey(itemID)){
			weight=itemIndegree.get(itemID);
		}
		return weight;
	}
	public  void addOneItemIndegree(Integer itemID){
		if(itemIndegree.containsKey(itemID)){
			itemIndegree.put(itemID, itemIndegree.get(itemID)+1);
		}else{
			itemIndegree.put(itemID, 2);
		}
	}
	public Long getFirstTime() {
		if (firstPool.isEmpty()) {
			return Parameter.startTime;
		} else {
			return firstPool.first().getTime();
		}
	}

	

	public void addItemToNextPool(Item item) {
		nextPool.add(item);
	}

	public void putToRecentPool(Item item) throws Exception {
		if(!Parameter.dataType.equals("patent")){
			if (recentPool.containsKey(item.getUid())) {
				if (recentPool.get(item.getUid()).containsKey(item.getTime())) {
					recentPool.get(item.getUid()).get(item.getTime()).add(item.getId());
				} else {
					Set<Integer> its = new HashSet<Integer>();
					its.add(item.getId());
					recentPool.get(item.getUid()).put(item.getTime(), its);
				}
			} else {
				Set<Integer> its = new HashSet<Integer>();
				its.add(item.getId());
				TreeMap<Long, Set<Integer>> ts = new TreeMap<Long, Set<Integer>>();
				ts.put(item.getTime(), its);
				recentPool.put(item.getUid(), ts);
				firstPool.add(item);
			}
			if (!firstPool.isEmpty()) {
				long firstTime = firstPool.first().getTime();
				long gap = item.getTime() - firstTime;
				while (gap > Parameter.windowSize && !firstPool.isEmpty()) {
					Item first = firstPool.pollFirst();
					recentPool.get(first.getUid()).get(first.getTime()).remove(first.getId());
					if (recentPool.get(first.getUid()).get(first.getTime()).isEmpty()) {
						recentPool.get(first.getUid()).remove(first.getTime());
					}
					if (recentPool.get(first.getUid()).isEmpty()) {
						recentPool.remove(first.getUid());
					}
					
					if(itemIndegree.containsKey(first.getId())){
						itemIndegree.remove(first.getId());
					}
					
					if(links!=null && links.containsKey(first.getId())){
						links.remove(first.getId());
					}
					
					if (!recentPool.isEmpty() && recentPool.containsKey(first.getUid())) {
						Entry<Long, Set<Integer>> next = recentPool.get(first.getUid()).firstEntry();

						if (next != null) {
							Integer nextID = (Integer) next.getValue().toArray()[0];
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
		
		
		

		

		
	}

	
	public Item removeFirstItemInNextPool() {
		return nextPool.pollFirst();
	}

	public Map<Integer, TreeMap<Long, Set<Integer>>> getRecentPool() {
		return recentPool;
	}

	public TreeSet<Item> getNextPool() {
		return nextPool;
	}



	public Neighbor findInRecentPool(Integer uid, Long time) {
		Neighbor result = new Neighbor();
		Entry<Long, Set<Integer>> iresult = recentPool.get(uid).floorEntry(time);
		Long itime = recentPool.get(uid).lowerKey(time);
		if (iresult == null) {
			iresult = recentPool.get(uid).ceilingEntry(time);
			itime = recentPool.get(uid).higherKey(time);
		}
		result.setCandidate(iresult.getValue());
		result.setTime(iresult.getKey());
		result.setNextTime(itime);
		return result;
	}

	
}
