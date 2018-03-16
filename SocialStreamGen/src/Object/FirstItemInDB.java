package Object;

import java.util.HashSet;
import java.util.Set;

/** 
 * @author Chengcheng Yu
 */
public class FirstItemInDB {
	Long time;
	Set<String> candidate=new HashSet<String>();
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Set<String> getCandidate() {
		return candidate;
	}
	public void setCandidate(Set<String> candidate) {
		this.candidate = candidate;
	}
	public void addItems(String itemID){
		candidate.add(itemID);
	}
	public void cleanAndAddItems(String itemID){
		candidate.clear();
		candidate.add(itemID);
	}
}
