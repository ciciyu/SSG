package Object;

import java.util.Random;

public class User {
	private Integer uid;
	private Double pRate;
	private Double rpRate;
	private Random pRandom;
	private Random rpRandom;


	public User(Integer uid, double pRate, double rpRate) {
		this.uid = uid;
		this.pRate = pRate;
		this.rpRate = rpRate;
		this.pRandom = new Random();
		this.rpRandom = new Random();
	}

	public User(Integer uid, double pRate) {
		this.uid = uid;
		this.pRate = pRate;
		this.pRandom = new Random();
	}
	public User(Integer uid) {
		this.uid = uid;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Double getpRate() {
		return pRate;
	}

	public void setpRate(double pRate) {
		this.pRate = pRate;
	}

	public Double getRpRate() {
		return rpRate;
	}

	public void setRpRate(double rpRate) {
		this.rpRate = rpRate;
	}

	public Random getpRandom() {
		return pRandom;
	}

	public void setpRandom(Random pRandom) {
		this.pRandom = pRandom;
	}

	public Random getRpRandom() {
		return rpRandom;
	}

	public void setRpRandom(Random rpRandom) {
		this.rpRandom = rpRandom;
	}

}