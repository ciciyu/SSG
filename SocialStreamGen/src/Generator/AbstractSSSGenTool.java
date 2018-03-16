package Generator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Common.generator.DiscreteIntegerGenerator;
import Common.generator.UniformIntegerGenerator;
import Object.Item;
import Object.Neighbor;

/**
 * A kind of social stream generator should extends the abstract class
 * SSGenUtil, and implements each member function, for working well.
 * 
 * @author Chengcheng Yu
 */
public abstract class AbstractSSSGenTool {

	public List<Integer> getFriends(Integer uid) {
		List<Integer> friends = new ArrayList<Integer>();
		if (Parameter.followerList.containsKey(uid)) {
			friends.addAll(Parameter.followerList.get(uid));
		}
		if (!Parameter.dataType.equals("weibo")) {
			friends.add(uid);
		}
		return friends;
	}

	public Set<Integer> getLinks(Integer uid, Long time, Integer linkNum) throws Exception {
		Set<Integer> result = new HashSet<Integer>();
		List<Integer> friends = getFriends(uid);
		friends.retainAll(Parameter.pool.getRecentPool().keySet());
//		 long timeInterval = time - Parameter.startTime;
		long timeInterval = time - Parameter.pool.getFirstTime();
		for (int i = 0; i < linkNum; i++) {
			Long iResult_time = null;
			long linkInterval=Parameter.LinkIntervalsGen.nextValue(timeInterval);

			long iTime = time - linkInterval;
			DiscreteIntegerGenerator<Integer> diGen = new DiscreteIntegerGenerator<Integer>();
			for (Integer f : friends) {
				Long tmpTime = iTime;
				while (tmpTime != null) {
					Neighbor neighbor = Parameter.pool.findInRecentPool(f, tmpTime);
					Set<Integer> candidate = neighbor.getCandidate();
					tmpTime = neighbor.getNextTime();
					if (candidate != null && !candidate.isEmpty()) {
						Set<Integer> cans = new HashSet<Integer>(candidate);
						if (diGen.isEmpty()) {
							cans.removeAll(result);
							if (!cans.isEmpty()) {
								for (Integer itemID : cans) {
									diGen.addValue(Parameter.pool.getItemIndegree(itemID), itemID);
								}
								iResult_time = neighbor.getTime();
								break;
							}
						} else {
							cans.removeAll(result);
							if (!cans.isEmpty()) {
								long gap_current = Math.abs(neighbor.getTime() - iTime);
								long gap_result = Math.abs(iResult_time - iTime);
								if (gap_current < gap_result) {
									diGen.clear();
									for (Integer itemID : cans) {
										diGen.addValue(Parameter.pool.getItemIndegree(itemID),
												itemID);
									}
									iResult_time = neighbor.getTime();
								} else if (gap_current == gap_result) {
									for (Integer itemID : cans) {
										diGen.addValue(Parameter.pool.getItemIndegree(itemID),
												itemID);
									}
								}
							}
							break;
						}
					}
				}
			}
			if (!diGen.isEmpty()) {
				long gap = Math.abs(iTime-iResult_time);
				if(gap<Parameter.LinkIntervalsGen.getLinkGranularity()*2.5){
					Integer itemID = diGen.nextValue();
					result.add(itemID);
					Parameter.pool.addOneItemIndegree(itemID);
				}
				
			}
		}
		Statistic.linkNum += result.size();
		return result;
	}

	double rIN = 2.746;
	public double beta = (rIN - 1) / rIN;

	public Set<Integer> getLinks_copyingEdges2(Integer uid, Long time, Integer linkNum)
			throws Exception {
		Set<Integer> result = new HashSet<Integer>();
		if (linkNum > 0) {
			if (Math.random() < Parameter.beta) {
				UniformIntegerGenerator gen = new UniformIntegerGenerator(0, Parameter.IDGen
						.lastValue());
				int loopNum = 0;
				while (gen.nextValue() != null && result.size() < linkNum && loopNum < linkNum
						+ 4) {
					loopNum++;
					result.add(gen.lastValue());
				}
//				return getLinks(uid, time, linkNum);
			} else {
				UniformIntegerGenerator gen = new UniformIntegerGenerator(Parameter.strartNum,
						Parameter.IDGen.lastValue());
				boolean isFull = false;
				int loopNum = 0;
				while (gen.nextValue() != null && !isFull && loopNum < linkNum) {
					loopNum++;
					Integer id = gen.lastValue() + Parameter.strartNum;
					if (Parameter.pool.links.containsKey(id)) {
						Set<Integer> links = Parameter.pool.links.get(id);
						if (links.size() < linkNum) {
							result.addAll(links);
							linkNum -= links.size();
						} else {
							for (Integer it : links) {
								linkNum--;
								result.add(it);
								if (linkNum == 0) {
									isFull = true;
									break;
								}
							}
						}
					} else {
						continue;
					}
				}
			}

		}

		Statistic.linkNum += result.size();
		return result;
	}

	/**
	 * generate the next time according to the current time (second) and uid
	 * (the id of user)
	 */
	public abstract Long nextTime(long currentTime, Integer uid) throws ParseException;

	/**
	 * generate the next time according to the current Item
	 */
	public abstract Long nextTime(Item item) throws ParseException;

	/**
	 * Get link size for each item
	 */
	public abstract Integer getLinkSize(Item item);

	/**
	 * Get links of item
	 */
	public abstract Set<Integer> getLinks(Item item) throws Exception;
}