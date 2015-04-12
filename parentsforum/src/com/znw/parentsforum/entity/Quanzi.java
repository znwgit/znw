package com.znw.parentsforum.entity;

import java.util.List;

public class Quanzi {
	private int id;
	private String name;
	private int personcount;
	private List<Tiezi> tiezilist;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPersoncount() {
		return personcount;
	}

	public void setPersoncount(int personcount) {
		this.personcount = personcount;
	}

	public List<Tiezi> getTiezilist() {
		return tiezilist;
	}

	public void setTiezilist(List<Tiezi> tiezilist) {
		this.tiezilist = tiezilist;
	}

	@Override
	public String toString() {
		return "Quanzi [id=" + id + ", name=" + name + ", personcount="
				+ personcount + ", tiezilist=" + tiezilist + "]";
	}

}
