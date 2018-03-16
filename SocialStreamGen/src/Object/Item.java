package Object;

import Generator.Util.TimeUtil;

public class Item{
	private Integer id;
	private Long time;
	private Integer uid;
	
	public Item(){
	}
	
	public Item(Integer id,Long time,Integer uid){
		this.id = id;
		this.time = time;
		this.uid = uid;
	}
	
	
	public Item(Long time, Integer uid) {
		this.time = time;
		this.uid = uid;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public String toString(String dataType){
		String result ="";
		TimeUtil timeUtil = new TimeUtil(dataType);
		result = id+"\t"+timeUtil.changeTimeToString(time)+" \t"+uid;
		
		return result;
	}
}
