package Generator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import Common.Generator.DiscreteIntegerGenerator;
import Object.Item;
import Object.ItemCom;
import Object.TaskResult;
import Object.WorkerTask;

/**
 * A kind of social stream generator should extends the abstract class
 * SSGenUtil, and implements each member function, for working well.
 * 
 * @author Chengcheng Yu
 */
public abstract class AbstractSSSGenTool {

	/**
	 * generate the next time according to the current time (second) and uid
	 * (the id of user)
	 */
	public abstract Long nextTime(long currentTime, String uid) throws ParseException;

	/**
	 * generate the next time according to the current Item
	 */
	public abstract Long nextTime(Item item) throws ParseException;

	/**
	 * Get link size for each item
	 */
	public abstract Integer getLinkSize(Item item);

	/**
	 * Generate ItemCom according to item for sending to master.
	 */
	public ItemCom generateItemCom(Item item) throws Exception {
		// TODO Auto-generated method stub
		int linkNum = Parameter.genTool.getLinkSize(item);
		int localLinkNum = Parameter.genTool.getLinkSizeLocal(item, linkNum);
		Set<String> links = Parameter.genTool.getLinks(item.getUid(), item.getTime(), localLinkNum);

	
		
		ItemCom sendItem = new ItemCom(item);
		sendItem.setLinkNum(linkNum - localLinkNum);
		sendItem.setLinks(links);

		return sendItem;
	}

	public Integer getLinkSizeLocal(Item item, Integer TotalLinkNum) {
		// TODO Auto-generated method stub
		int count = 0;
		if (Parameter.followerList.containsKey(item.getUid())) {
			if (TotalLinkNum > 1) {
				count = (int) (TotalLinkNum * Parameter.followerList.get(item.getUid()).get_pro());
			} else if (TotalLinkNum == 1) {
				if (Math.random() < Parameter.followerList.get(item.getUid()).get_pro()) {
					count = 1;
				}
			}
		}
		return count;
	}

	/**
	 * Get links for a task.
	 */
	public TaskResult getTaskLink(WorkerTask task) throws Exception {
		// TODO Auto-generated method stub
		TaskResult result = new TaskResult();
		result.setId(task.getId());
		result.setLinks(getLinks(task.getUid(), task.getTime(), task.getLinkNum()));

		return result;
	}

	public List<String> getFriends(String uid) {
		List<String> friends = new ArrayList<String>();
		if (Parameter.followerList.containsKey(uid)) {
			friends.addAll(Parameter.followerList.get(uid).getMembers());
		}
		return friends;
	}

	//int count = 5;

	public Set<String> getLinks(String uid, Long time, Integer linkNum) {
		Set<String> result = new HashSet<String>();
		List<String> friends = getFriends(uid);

		friends.retainAll(Parameter.pool.getRecentPool().keySet());
//		long timeInterval = time - Parameter.workerInfo.getStartTime();
		long timeInterval =time-Parameter.pool.getFirstTime();
				
		DiscreteIntegerGenerator<String> diGen = new DiscreteIntegerGenerator<String>();
		for (int i = 0; i < linkNum; i++) {
			Long iResult_time = null;
			long iTime = time - Parameter.LinkIntervalsGen.nextValue(timeInterval);

			for (String f : friends) {
				Long tmpTime = iTime;
				while (tmpTime != null) {
					Entry<Long, Set<String>> candidate =null;
					Long tmp =null;
					if(Parameter.pool.getRecentPool().containsKey(f)){
						candidate =Parameter.pool.getRecentPool().get(f).floorEntry(tmpTime);
						tmp = Parameter.pool.getRecentPool().get(f).lowerKey(tmpTime);
						if (candidate == null) {
							candidate = Parameter.pool.getRecentPool().get(f).ceilingEntry(tmpTime);
							tmp = Parameter.pool.getRecentPool().get(f).higherKey(tmpTime);
						}
					}
					tmpTime = tmp;
					
					if (candidate != null &&candidate.getValue() != null) {
						Set<String> cans = new HashSet<String>(candidate.getValue());
						if (diGen.isEmpty()) {
							cans.removeAll(result);
							if (!cans.isEmpty()) {
								for (String itemID : cans) {
									diGen.addValue(Parameter.getItemIndegree(itemID), itemID);
								}
								iResult_time = candidate.getKey();
								break;
							}
						} else {
							cans.removeAll(result);
							if (!cans.isEmpty()) {
								long gap_current = Math.abs(candidate.getKey() - iTime);
								long gap_result = Math.abs(iResult_time - iTime);
								if (gap_current < gap_result) {
									diGen.clear();
									for (String itemID : cans) {
										diGen.addValue(Parameter.getItemIndegree(itemID), itemID);
									}
									iResult_time = candidate.getKey();
								} else if (gap_current == gap_result) {
									for (String itemID : cans) {
										diGen.addValue(Parameter.getItemIndegree(itemID), itemID);
									}
								}
							}
							break;
						}
					}
				}
			}
			if (!diGen.isEmpty()) {
				String itemID = diGen.nextValue();
				result.add(itemID);
				Parameter.addOneItemIndegree(itemID);
			}
			diGen.clear();
		}
		
		
		
		return result;

	}

}