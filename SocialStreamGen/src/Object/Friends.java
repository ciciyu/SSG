package Object;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chengcheng Yu
 */
public class Friends {
	Set<Integer> _members = new HashSet<Integer>();
	Double _sumRate ;

	public Friends(Set<Integer> members, Double sumRate) {
		_members = members;
		_sumRate = sumRate;
	}

	public void addMember(Integer uid) {
		_members.add(uid);
	}

	public void addRate(double rate) {
		_sumRate += rate;
	}

	public Set<Integer> getMembers() {
		return _members;
	}

	public void setMembers(Set<Integer> members) {
		this._members = members;
	}

	public Double getSumRate() {
		return _sumRate;
	}

	public void setSumRate(Double sumRate) {
		this._sumRate = sumRate;
	}

}
