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
}