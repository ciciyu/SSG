package Generator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Object.Item;

/**
 * SSSGenTool_Patent is used to generate patent document stream.
 * 
 * @author Chengcheng Yu
 */
public class PatentGenToolFactory extends AbstractSSSGenTool {

	Map<Integer, Long> user_realTime = new HashMap<Integer, Long>();

	

	/*
	 * The function of nextTime_patent is used to generate the next time of
	 * patent. The process of patent publishing is a Counting process, and the
	 * counting process is a homogeneous poisson process. The intervals between
	 * continual patents is a multiple of 7.
	 */
	@Override

	public Long nextTime(long time, Integer uid) throws ParseException {
		// TODO Auto-generated method stub
		double pRate = Parameter.users.get(uid).getpRate();
		double r = Parameter.users.get(uid).getpRandom().nextDouble();
		double intervals = -Math.log(r) / pRate;
		long multiple = (long) Math.floor(intervals / (7 * 24 * 3600));
		time += multiple * (7 * 24 * 3600);
		if (time <= Parameter.endTime) {
			user_realTime.put(uid, time);
			return time;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.AbstractSSSGenTool#nextTime(Object.Item)
	 */
	@Override
	public Long nextTime(Item item) throws ParseException {
		// TODO Auto-generated method stub
//		double pRate = Parameter.users.get(item.getUid()).getpRate();
		Long time = nextTime_Periodic(item.getUid());
//		long time = user_realTime.get(item.getUid());
//		double r = Parameter.users.get(item.getUid()).getpRandom().nextDouble();
//		long intervals = (long) (-Math.log(r) / pRate);
//		time += intervals;
		if (time!=null && time <= Parameter.endTime) {
			if (time <= item.getTime()) {
				return item.getTime();
			} else {
				long gap = time - item.getTime();
				long multiple = (long) Math.floor(gap / (7 * 24 * 3600));
				long nextTime = item.getTime() + multiple * (7 * 24 * 3600);
				if (nextTime <= Parameter.endTime) {
					user_realTime.put(item.getUid(), time);
					return nextTime;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
	public Long nextTime_Periodic(Integer uid) throws ParseException {
		// TODO Auto-generated method stub
		double pRate = Parameter.users.get(uid).getpRate();
		long time = user_realTime.get(uid);
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
	private double getFactor(long time) {
		Calendar e = Calendar.getInstance();
		e.setTime(new Date(time * 1000));
		double w = Parameter.week.get(e.get(Calendar.WEEK_OF_YEAR)-1);
		return w;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.SSGenUtil#getLinkSize(Object.Item)
	 */
	@Override
	public Integer getLinkSize(Item item) {
		// TODO Auto-generated method stub
		int size = 0;
		if (Parameter.IDGen.lastValue() > Parameter.linksize0) {
			size = Parameter.ItemOutdegreeGen.nextValue();
		}

		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Generator.SSGenUtil#getLinks(Object.Item, java.lang.Integer)
	 */
	@Override
	
	public Set<Integer> getLinks(Item item) throws Exception {
		// TODO Auto-generated method stub
//		Set<String> result = new HashSet<String>();

		int linkNum = getLinkSize(item);
		
		if(linkNum>0 && Parameter.strart==null &&Parameter.strartNum==null){
			Parameter.strart=item.getTime();
			Parameter.strartNum = item.getId();
		}
		
		
		Set<Integer> result = getLinks_copyingEdges2(item.getUid(),item.getTime(),linkNum); 
//		Set<Integer> result = getLinks(item.getUid(),item.getTime(),linkNum); 
		if(result.size()>0){
			Parameter.pool.links.put(item.getId(), result);
		}
		return result;
	}

	
}
