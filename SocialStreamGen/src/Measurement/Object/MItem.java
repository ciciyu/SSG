package Measurement.Object;


/** 
 * @author Chengcheng Yu
 */
public class MItem {
	String DAG_id;
	Integer height;
	
	public MItem(String DAG_id,Integer height){
		this.DAG_id = DAG_id;
		this.height = height;
	}
	
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getDAG_id() {
		return DAG_id;
	}
	public void setDAG_id(String dAG_id) {
		DAG_id = dAG_id;
	}
	

}
