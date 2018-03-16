package Generator.Util;

import java.util.Comparator;

import Object.WorkerTask;

/** 
 * @ClassName: ComparatorByTimeReverseOrder 
 * @Description: TODO
 * @author Chengcheng Yu
 *  
 */
public class ComparatorWorkerTask implements Comparator<WorkerTask> {

	
	public int compare(WorkerTask t1, WorkerTask t2) {
		// TODO Auto-generated method stub
		if(t1.getTime().equals(t2.getTime())){
			return t1.getId().compareTo(t2.getId());
		}else{
			return t1.getTime().compareTo(t2.getTime());
		}
	}

}