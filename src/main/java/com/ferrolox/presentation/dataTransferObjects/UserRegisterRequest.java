package com.ferrolox.presentation.dataTransferObjects;

public record UserRegisterRequest(
	String username,
	String alias,
	String password
) {}