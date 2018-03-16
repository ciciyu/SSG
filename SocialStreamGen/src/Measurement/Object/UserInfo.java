package Measurement.Object;

/**
 * @author Chengcheng Yu
 */
public class UserInfo {
	Integer postNum = 0;
	Long lastTime = null;
	
	public UserInfo() {
	}
	public UserInfo(Integer postNum, Long lastTime) {
		this.postNum = postNum;
		this.lastTime = lastTime;
	}
	public Integer getPostNum() {
		return postNum;
	}
	public void setPostNum(Integer postNum) {
		this.postNum = postNum;
	}
	public Long getLastTime() {
		return lastTime;
	}
	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}

}
