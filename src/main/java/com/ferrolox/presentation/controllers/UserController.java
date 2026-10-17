package com.ferrolox.presentation.controllers;

import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.ConflictingCredentialsException;
import com.ferrolox.foundation.exceptions.DatabaseException;
import com.ferrolox.foundation.exceptions.InvalidCredentialsException;
import com.ferrolox.foundation.exceptions.WeakCredentialsException;
import com.ferrolox.presentation.dataTransferObjects.UserLoginRequest;
import com.ferrolox.presentation.dataTransferObjects.UserRegisterRequest;
import com.ferrolox.service.UserService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	public void login(Context context) throws InvalidCredentialsException, DatabaseException {
		UserLoginRequest userLoginRequest = context.bodyAsClass(UserLoginRequest.class);

		String token = userService.login(new User(0, userLoginRequest.username(), null, userLoginRequest.password()));

		context.json(token);
	}

	public void register(Context context) throws WeakCredentialsException, ConflictingCredentialsException, DatabaseException {
		UserRegisterRequest userRegisterRequest = context.bodyAsClass(UserRegisterRequest.class);

		String token = userService.register(new User(0, userRegisterRequest.username(), userRegisterRequest.alias(), userRegisterRequest.password()));

		context.status(HttpStatus.CREATED).json(token);
	}
}