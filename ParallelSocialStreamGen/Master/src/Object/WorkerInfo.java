/**   
* @Title: SlaveInfo.java 
* @Package Object 
* @Description: TODO
* @author Chengcheng Yu
* @date 2016年5月17日 
* @version V1.0   
*/
package Object;

/** 
* @author Chengcheng Yu
* @version 2016年5月17日 下午12:43:55
*/
/**
 * @ClassName: SlaveInfo
 * @Description: TODO
 * @author Chengcheng Yu
 * 
 */
public class WorkerInfo {
	String path;
	Long startTime;
	Long endTime;
	Integer workerID;
	String userNum;
	Integer workerNum;
	String dataType;
	Long windowSize;

	public Long getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Long windowSize) {
		this.windowSize = windowSize;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Integer getWorkerID() {
		return workerID;
	}

	public void setWorkerID(Integer workerID) {
		this.workerID = workerID;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public Integer getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(Integer workerNum) {
		this.workerNum = workerNum;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String toString() {
		return path + "," + startTime + "," + endTime + "," + userNum + "," + dataType + "," + workerID + ","
				+ workerNum+ "," +windowSize;
	}

}
