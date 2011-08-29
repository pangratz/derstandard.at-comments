package com.pangratz.derstandardatcomments.model;

public class Rating {

	private int brilliant;
	private int unnecessary;
	private int interesting;

	public Rating() {
		super();
	}

	public Rating(int brilliant, int interesting, int unnecessary) {
		this();
		this.brilliant = brilliant;
		this.interesting = interesting;
		this.unnecessary = unnecessary;
	}

	public int getBrilliant() {
		return brilliant;
	}

	public int getInteresting() {
		return interesting;
	}

	public int getUnnecessary() {
		return unnecessary;
	}

	public void setBrilliant(int brilliant) {
		this.brilliant = brilliant;
	}

	public void setInteresting(int interesting) {
		this.interesting = interesting;
	}

	public void setUnnecessary(int unnecessary) {
		this.unnecessary = unnecessary;
	}

}
