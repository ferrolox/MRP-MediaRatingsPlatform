package com.ferrolox.presentation.dataTransferObjects;

public record UserLoginRequest (
	String username,
	String password
) {}