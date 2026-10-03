package com.ferrolox.presentation;

import io.javalin.http.Context;

public class HomeController {

	public void home(Context context) {
		context.json("Welcome to the MRP API!");
	}
}