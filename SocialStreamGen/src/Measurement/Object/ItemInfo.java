package Measurement.Object;

/** 
 * @author Chengcheng Yu
 */
public class ItemInfo {
	Long time;
	Integer indegree=0;
	
	public ItemInfo() {
	}
	
	public ItemInfo(Long time, Integer indegree) {
		this.time = time;
		this.indegree = indegree;
	}
	public  Long getTime() {
		return time;
	}
	public  void setTime(Long time) {
		this.time = time;
	}
	public  Integer getIndegree() {
		return indegree;
	}
	public  void setIndegree(Integer indegree) {
		this.indegree = indegree;
	}
	
	
}
