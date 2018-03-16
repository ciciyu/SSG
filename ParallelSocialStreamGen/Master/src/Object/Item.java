package Object;

import java.util.Set;

import Generator.Parameter;

public class Item{
	private String id;
	private Long time;
	private String uid;
	private Integer linkNum;
	private Set<String> links;
	private Integer workerID;

	
	public Integer getWorkerID() {
		return workerID;
	}

	public void setWorkerID(Integer workerID) {
		this.workerID = workerID;
	}

	public Set<String> getLinks() {
		return links;
	}

	public void setLinks(Set<String> links) {
		this.links = links;
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

	public Integer getLinkNum() {
		return linkNum;
	}

	public void setLinkNum(Integer linkNum) {
		this.linkNum = linkNum;
	}

	public String toString(){
		String result =id+","+time+","+uid+","+linkNum;
		if(links!=null){
			for(String it:links){
				result +=","+it;
			}
		}
		return result;
	}
	
	public String toOut(){
		String result = id+"\t"+Parameter.timeUtil.changeTimeToString(time)+" \t"+uid;
//		if(links!=null){
//			for(String it:links){
//				result+="\t"+it;
//			}
//		}
		return result;
	}
}
