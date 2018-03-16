package Common.generator;

/**
 * Generates integers randomly uniform from an interval.
 */
public class UniformIntegerGenerator extends Generator<Integer> 
{
	Integer _lb,_ub;
	
	/**
	 * Creates a generator that will return integers uniformly randomly from the interval [lb,ub] inclusive (that is, lb and ub are possible values)
	 *
	 * @param lb the lower bound (inclusive) of generated values
	 * @param ub the upper bound (inclusive) of generated values
	 */
	public UniformIntegerGenerator(int lb, int ub)
	{
		_lb=lb;
		_ub=ub;
	}
	public UniformIntegerGenerator()
	{
	}
	public Integer nextValue() 
	{
		if(_ub!=null && _lb!=null){
			_last=getRandom().nextInt(_ub-_lb+1)+_lb;
		}else{
			System.out.println("Please set upbound and lowbound!");
		}
		
		return _last;
	}

	public Integer lastValue() {
		// TODO Auto-generated method stub
		return _last;
	}

	public int get_lb() {
		return _lb;
	}
	public void set_lb(int _lb) {
		this._lb = _lb;
	}
	public int get_ub() {
		return _ub;
	}
	public void set_ub(int _ub) {
		this._ub = _ub;
	}

	

	
}
