package Generator;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import Object.Item;

/**
 * SSSGenTool_Patent is used to generate patent document stream.
 * 
 * @author Chengcheng Yu
 */
public class PatentGenToolFactory extends AbstractSSSGenTool {

	Map<String, Long> user_realTime = new HashMap<String, Long>();

	

	/*
	 * The function of nextTime_patent is used to generate the next time of
	 * patent. The process of patent publishing is a Counting process, and the
	 * counting process is a homogeneous poisson process. The intervals between
	 * continual patents is a multiple of 7.
	 */
	@Override

	public Long nextTime(long time, String uid) throws ParseException {
		// TODO Auto-generated method stub
		double pRate = Parameter.users.get(uid).getpRate();
		double r = Parameter.users.get(uid).getpRandom().nextDouble();
		double intervals = -Math.log(r) / pRate;
		long multiple = (long) Math.floor(intervals / (7 * 24 * 3600));
		time += multiple * (7 * 24 * 3600);
		if (time <= Parameter.workerInfo.getEndTime()) {
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
		double pRate = Parameter.users.get(item.getUid()).getpRate();
		long time = user_realTime.get(item.getUid());
		double r = Parameter.users.get(item.getUid()).getpRandom().nextDouble();
		long intervals = (long) (-Math.log(r) / pRate);
		time += intervals;
		if (time <= Parameter.workerInfo.getEndTime()) {
			if (time <= item.getTime()) {
				return item.getTime();
			} else {
				long gap = time - item.getTime();
				long multiple = (long) Math.floor(gap / (7 * 24 * 3600));
				long nextTime = item.getTime() + multiple * (7 * 24 * 3600);
				if (nextTime <= Parameter.workerInfo.getEndTime()) {
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

	

}
