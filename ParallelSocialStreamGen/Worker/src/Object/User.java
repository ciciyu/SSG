package Object;

import java.util.Random;

public class User {
	private String uid;
	private double pRate;
	private double rpRate;
	private Random pRandom;
	private Random rpRandom;
	

	public User(String uid, double pRate, double rpRate) {
		this.uid = uid;
		this.pRate = pRate;
		this.rpRate = rpRate;
		this.pRandom = new Random();
		this.rpRandom = new Random();
	}

	public User(String uid, double pRate) {
		this.uid = uid;
		this.pRate = pRate;
		this.pRandom = new Random();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public double getpRate() {
		return pRate;
	}

	public void setpRate(double pRate) {
		this.pRate = pRate;
	}

	public double getRpRate() {
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