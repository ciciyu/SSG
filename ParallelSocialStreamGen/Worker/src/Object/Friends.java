package Object;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chengcheng Yu
 */
public class Friends {
	Double _pro;
	Set<String> _members = new HashSet<String>();

	public Friends(){
		
	}
	
	public Friends(Set<String> members,Double pro) {
		_members = members;
		_pro = pro;
	}

	public Double get_pro() {
		return _pro;
	}

	public void set_pro(Double _pro) {
		this._pro = _pro;
	}

	public void addMember(String uid) {
		_members.add(uid);
	}

	
	public Set<String> getMembers() {
		return _members;
	}

	public void setMembers(Set<String> members) {
		this._members = members;
	}


	
}
