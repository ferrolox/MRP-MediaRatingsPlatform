package com.ferrolox.domain;

public class User {
	private int id;
	private String username;
	private String alias;
	private String password;

	public User(int id, String username, String alias, String password) {
		this.id = id;
		this.username = username;
		this.alias = alias;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}