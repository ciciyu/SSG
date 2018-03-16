package Measurement.Object;


import java.util.HashSet;
import java.util.Set;

/** 
 * @author Chengcheng Yu
 */
public class DAG {
	private String id;
	private Set<String> members;
	private Integer height;
	
	public DAG(String id,Set<String> members,Integer height) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.members = members;
		this.height = height;
	}
	
	
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<String> getMembers() {
		if(members==null){
			members = new HashSet<String>();
		}
		return members;
	}
	public void setMembers(Set<String> members) {
		this.members = members;
	}
	
}
