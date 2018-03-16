package Object;

/** 
* @author Chengcheng Yu
* @version 2016年5月31日 下午1:14:05
*/
/**
 * @ClassName: Task
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class WorkerTask {
	private String id;
	private Long time;
	private String uid;
	private Integer linkNum;

	public WorkerTask(String id, Long time, String uid, Integer linkNum) {
		this.id = id;
		this.time = time;
		this.uid = uid;
		this.linkNum = linkNum;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	

	public String toString() {
		return id + "," + time + "," + uid + "," + linkNum ;
	}

}
