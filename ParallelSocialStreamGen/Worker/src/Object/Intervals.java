package Object;

/** 
 * @author Chengcheng Yu
 */
public class Intervals {

	Long _minTime;
	Long _maxTime;

	public Intervals(Long minTime, Long maxTime) {
		_minTime = minTime;
		_maxTime = maxTime;
	}

	public Long get_minTime() {
		return _minTime;
	}

	public Long get_maxTime() {
		return _maxTime;
	}

	public void set_minTime(Long _minTime) {
		this._minTime = _minTime;
	}

	public void set_maxTime(Long _maxTime) {
		this._maxTime = _maxTime;
	}

}