package Generator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import Object.Item;

/**
 * SSSGenTool_Tweet is used to generate tweet stream.
 * 
 * @author Chengcheng Yu
 */
public class TweetGenToolFactory extends AbstractSSSGenTool {

	
	/**
	 * The function of nextTime_weibo is used to generate the next time of
	 * weibo. The process of Users publish messages is a Counting process, and
	 * the counting process is a Non-homogeneous poisson process. So the
	 * distribution of intervals between messages is exponential distribution.
	 */
	@Override
	public Long nextTime(long time, String uid) throws ParseException {
		// TODO Auto-generated method stub
//		System.out.println(Parameter.timeUtil.changeTimeToString(time));
		double pRate = Parameter.users.get(uid).getpRate();
		while (time < Parameter.workerInfo.getEndTime()) {
			double r = Parameter.users.get(uid).getpRandom().nextDouble();
			time += ((-Math.log(r) / (Parameter.maxFactor * pRate)));
			if (time <= Parameter.workerInfo.getEndTime()) {
				if (Math.random() < (getFactor(time) / Parameter.maxFactor)) {
					return time;
				}
			} else {
				return null;
			}
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.SSGenUtil#getLinkSize(Object.Item)
	 */
	@Override
	public Integer getLinkSize(Item item) {
		// TODO Auto-generated method stub
		double rRate = Parameter.users.get(item.getUid()).getRpRate();
		if (Parameter.users.get(item.getUid()).getRpRandom().nextDouble() <= rRate) {
			return 1;
		} else {
			return 0;
		}
	}

	// DiscreteGenerator<Item> dGen = new DiscreteGenerator<Item>(false, false,
	// false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.SSGenUtil#getLinks(Object.Item, java.lang.Integer)
	 */
	

//	public ItemCom generateItemCom(Item item) throws Exception {
//		ItemCom itemSend = new ItemCom(item);
//		int linkNum = getLinkSize(item);
//		itemSend.setLinkNum(linkNum);
//		if (linkNum == 1) {
//			if (Parameter.followerList.containsKey(item.getUid())) {
//				if (Math.random() <= Parameter.followerList.get(item.getUid()).get_pro()) {
//					itemSend.setLinkNum(0);
//					itemSend.setLinks(getLinks(item.getUid(),item.getTime(),1));
//					
//				}
//			}
//		}
//		return itemSend;
//
//	}

//	public Set<String> getLinks(Task task) throws Exception {
//
//		Set<String> result = new HashSet<String>();
//		if (task.getLinkNum() > 0) {
//			String it = getAttachment(task);
//			if (it != null) {
//				result.add(it);
//			}
//		}
//		return result;
//	}

//	public Long getRandomTime(Intervals intervals) {
//		return new Random().nextInt((int) (intervals.get_maxTime() - intervals.get_minTime()))
//				+ intervals.get_minTime();
//	}

//	public Intervals getIntervals(String uid,long time) {
//		Long minTime = null;
//		Long maxTime = null;
//		Long timeInterval = time - Parameter.workerInfo.getStartTime();
//		long gap = (long) (500 / (Parameter.followerList.get(uid).getSumRate()
//				/ Parameter.followerList.get(uid).get_pro()));
//
//		if (timeInterval > Parameter.LinkIntervalsGen.getLinkGranularity()) {
//
//			long interval = Parameter.LinkIntervalsGen.nextValue(timeInterval);
//			if (timeInterval >= gap) {
//				long min_intervals = time - interval - Parameter.LinkIntervalsGen.getLinkGranularity();
//				long min_gap = time - gap;
//				long max_gap = time;
//
//				if (min_intervals >= max_gap) {
//					minTime = min_gap;
//					maxTime = max_gap;
//				} else {
//					minTime = min_intervals;
//					maxTime = max_gap;
//				}
//
//			} else {
//				maxTime = time - interval;
//				minTime = maxTime - Parameter.LinkIntervalsGen.getLinkGranularity();
//			}
//
//		} else {
//			if (timeInterval > gap) {
//				minTime = time - gap;
//			} else {
//				minTime = Parameter.workerInfo.getStartTime();
//			}
//			maxTime = time;
//		}
//
//		return new Intervals(minTime, maxTime);
//	}

	/**
	 * The function of getFactor is used to get adjustment factor
	 */
	private double getFactor(long time) {
		Calendar e = Calendar.getInstance();
		e.setTime(new Date(time * 1000));
		double h = Parameter.hour.get(e.get(Calendar.HOUR_OF_DAY));
		double d = Parameter.day.get(e.get(Calendar.DAY_OF_WEEK) - 1);
		return h * d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.AbstractSSSGenTool#nextTime(Object.Item)
	 */
	@Override
	public Long nextTime(Item item) throws ParseException {
		// TODO Auto-generated method stub
		return nextTime(item.getTime(), item.getUid());
	}

	

}
