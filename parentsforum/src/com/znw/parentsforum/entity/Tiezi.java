package com.znw.parentsforum.entity;

public class Tiezi {
	private int tid;
	private String tname;
	private String createtiem;
	private int backcount;

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getCreatetiem() {
		return createtiem;
	}

	public void setCreatetiem(String createtiem) {
		this.createtiem = createtiem;
	}

	public int getBackcount() {
		return backcount;
	}

	public void setBackcount(int backcount) {
		this.backcount = backcount;
	}

	@Override
	public String toString() {
		return "Tiezi [tid=" + tid + ", name=" + tname + ", createtiem="
				+ createtiem + ", backcount=" + backcount + "]";
	}

}
