package Object;

import java.util.Set;

/** 
 * @author Chengcheng Yu
 */
public class Neighbor {
//	Entry<Long, Set<String>> dandidate;
	Long time;
	Set<Integer> candidate;
	Long nextTime;
	
	public Neighbor() {
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Set<Integer> getCandidate() {
		return candidate;
	}
	public void setCandidate(Set<Integer> candidate) {
		this.candidate = candidate;
	}
	public Long getNextTime() {
		return nextTime;
	}
	public void setNextTime(Long nextTime) {
		this.nextTime = nextTime;
	}
	
	
}
