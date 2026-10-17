package com.ferrolox.service;

import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.*;
import com.ferrolox.persistence.repositories.UserRepository;
import com.ferrolox.security.*;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{12,}$");

	private final UserRepository userRepository;
	private final HashingService hashingService;
	private final TokenService tokenService;

	public UserService(UserRepository userRepository, HashingService hashingService, TokenService tokenService) {
		this.userRepository = userRepository;
		this.hashingService = hashingService;
		this.tokenService = tokenService;
	}

	public String login(User user) throws InvalidCredentialsException, DatabaseException {
		Optional<User> result = userRepository.findByUsername(user.getUsername());

		if (result.isPresent() && hashingService.verify(user.getPassword(), result.get().getPassword())) {
			return tokenService.generate(result.get());
		}

		throw new InvalidCredentialsException("Invalid username or password");
	}

	public String register(User user) throws ConflictingCredentialsException, WeakCredentialsException, DatabaseException {
		Optional<User> result = userRepository.findByUsername(user.getUsername());

		if (result.isPresent()) throw new ConflictingCredentialsException("Username already in use");
		if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) throw new WeakCredentialsException("Password does not meet complexity requirements");

		user.setPassword(hashingService.generate(user.getPassword()));
		int userId = userRepository.save(user);

		user.setId(userId);

		return tokenService.generate(user);
	}
}