package Object;

import Generator.Parameter;

public class Item{
	private String id;
	private Long time;
	private String uid;
	
	public Item(String id,Long time,String uid){
		this.id = id;
		this.time = time;
		this.uid = uid;
	}
	
	public Item(Long time, String uid) {
		this.time = time;
		this.uid = uid;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	public String toString(){
		
		return id+"\t"+ Parameter.timeUtil.changeTimeToString(time)+" \t"+uid;
	}
}
