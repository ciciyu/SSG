package Generator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

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
	public Long nextTime(long time, Integer uid) throws ParseException {
		// TODO Auto-generated method stub
		double pRate = Parameter.users.get(uid).getpRate();
		while (time < Parameter.endTime) {
			double r = Parameter.users.get(uid).getpRandom().nextDouble();
			time += ((-Math.log(r) / (Parameter.maxFactor * pRate)));
			if (time <= Parameter.endTime) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.SSGenUtil#getLinks(Object.Item, java.lang.Integer)
	 */
	@Override

	public Set<Integer> getLinks(Item item) throws Exception {
		int linkNum = getLinkSize(item);
		return getLinks(item.getUid(),item.getTime(),linkNum);

	}


	// private String getRtTweet_pre(Item item, Intervals intervals, Set<String>
	// friends) {
	// String result = null;
	// TreeSet<Item> feed = new TreeSet<Item>(new
	// ComparatorByTimeReverseOrder());
	// for (String uid : friends) {
	// String mid =
	// para.getPool().getRecentPool().get(uid).lastEntry().getValue();
	// Item iitem = para.getPool().getItems().get(mid);
	// if (iitem != null) {
	// if (feed.size() <= para.getCutLength()) {
	// feed.add(iitem);
	// } else if (iitem.getTime() > feed.last().getTime()) {
	// feed.add(iitem);
	// feed.remove(feed.last());
	// }
	// }
	// }
	//
	//
	//
	//
	// boolean isIntervals = false;
	// while (feed.size() > 0) {
	// Item first = feed.pollFirst();
	// if (dGen.getSize() < para.getCutLength() ) {
	// if (first.getTime() > intervals.get_maxTime()) {
	// break;
	// }
	// if (first.getTime() >= intervals.get_minTime() && first.getTime() <=
	// intervals
	// .get_maxTime() ) {
	//// isIntervals = true;
	//// dGen.clear();
	// dGen.addValue((double) first.getIndegree(), first);
	// }
	//// dGen.addValue((double) first.getIndegree(), first);
	// } else {
	// break;
	// }
	//
	// Item lowerItem = para.getPool().getPreItemInRecentPool(first);
	// if (lowerItem != null) {
	// feed.add(lowerItem);
	// }
	// }
	//
	// if (!dGen.isEmpty()) {
	// Item it = dGen.nextValue();
	// it.indegreeAddOne();
	// result = it.getId();
	// }
	// dGen.clear();
	// return result;
	// }

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

	// public Intervals getIntervals(Item item) {
	// Long minTime = null;
	// Long maxTime = null;
	// double sumPRate = para.getFollowerList().get(item.getUid()).getSumRate()
	// + Parameter.users
	// .get(item.getUid()).getpRate();
	//
	// long gap = (long) (10 / sumPRate);
	// long granularity = para.getLinkIntervalsGen().getLinkGranularity();
	//
	// Long timeInterval = item.getTime() - para.getStartTime();
	// if (timeInterval <= gap) {
	// minTime = para.getStartTime();
	// maxTime = item.getTime();
	// } else if(timeInterval <= granularity){
	// maxTime = item.getTime();
	// minTime = maxTime-gap;
	// }else{
	// long interval = para.getLinkIntervalsGen().nextValue(timeInterval);
	// long tmp_min = item.getTime() - interval;
	// long tmp_max = tmp_min+granularity;
	// if(granularity>=gap){
	// minTime = new Random().nextInt((int) (tmp_max-gap))+tmp_min;
	// maxTime =minTime+gap;
	// }else{
	// long plus = gap-granularity;
	// long preGap = tmp_min-para.getStartTime();
	// long behindGap = item.getTime()-tmp_max;
	// if(preGap<plus/2){
	// minTime = para.getStartTime();
	// maxTime = minTime + gap;
	// }else if(behindGap<plus/2){
	// maxTime = item.getTime();
	// minTime = maxTime - gap;
	// }else{
	// minTime = tmp_min - plus / 2;
	// maxTime = tmp_max + plus / 2;
	// }
	// }
	// }
	//
	// return new Intervals(minTime, maxTime);
	// }

//	public Intervals getIntervals(Item item) {
//		Long minTime = null;
//		Long maxTime = null;
//		Long timeInterval = item.getTime() - para.getStartTime();
//		double sumPRate = 0;
//		if (para.getFollowerList().containsKey(item.getUid())) {
//			sumPRate = para.getFollowerList().get(item.getUid()).getSumRate();
//		} else {
//			sumPRate = para.getFolloweeList().get(item.getUid()).getSumRate();
//		}
//		//sumPRate += Parameter.users.get(item.getUid()).getpRate();
//
//		long gap = (long) (500 / sumPRate);
//
//		if (timeInterval > para.getLinkIntervalsGen().getLinkGranularity()) {
//
//			long interval = para.getLinkIntervalsGen().nextValue(timeInterval);
//			if (timeInterval >= gap) {
//				long min_intervals = item.getTime() - interval - para.getLinkIntervalsGen()
//						.getLinkGranularity();
//				long min_gap = item.getTime() - gap;
//				long max_gap = item.getTime();
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
//				maxTime = item.getTime() - interval;
//				minTime = maxTime - para.getLinkIntervalsGen().getLinkGranularity();
//			}
//
//		} else {
//			if (timeInterval > gap) {
//				minTime = item.getTime() - gap;
//			} else {
//				minTime = para.getStartTime();
//			}
//			maxTime = item.getTime();
//		}
//
//		return new Intervals(minTime, maxTime);
//	}
	// public Intervals getIntervals(Item item) {
	// Long minTime = null;
	// Long maxTime = null;
	//
	//
	// long timeInterval = item.getTime() - para.getStartTime();
	// if (timeInterval > para.getLinkIntervalsGen().getLinkGranularity()) {
	//
	// long interval = para.getLinkIntervalsGen().nextValue(timeInterval);
	// minTime = item.getTime() - interval;
	// maxTime = minTime + para.getLinkIntervalsGen().getLinkGranularity();
	// }else{
	// minTime=para.getStartTime();
	// maxTime = item.getTime();
	// }
	//
	// return new Intervals(minTime, maxTime);
	// }
}
