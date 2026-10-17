package com.ferrolox.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class HashingService {

	private static final int SALT_LENGTH = 16;
	private static final int HASH_LENGTH = 32;
	private static final int ITERATIONS = 3;
	private static final int MEMORY_KB = 65536;
	private static final int PARALLELISM = 1;

	private final String pepper;

	public HashingService(String pepper) {
		this.pepper = pepper;
	}

	public String generate(String password) {
		byte[] salt = new byte[SALT_LENGTH];
		new SecureRandom().nextBytes(salt);

		byte[] hash = derive(password + pepper, salt);

		return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
	}

	public boolean verify(String password, String storedHash) {
		String[] parts = storedHash.split("\\$");

		byte[] salt = Base64.getDecoder().decode(parts[0]);
		byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
		byte[] actualHash = derive(password + pepper, salt);

		return MessageDigest.isEqual(actualHash, expectedHash);
	}

	private byte[] derive(String password, byte[] salt) {
		Argon2Parameters parameters = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
							                              .withSalt(salt)
							                              .withIterations(ITERATIONS)
							                              .withMemoryAsKB(MEMORY_KB)
							                              .withParallelism(PARALLELISM)
							                              .build();

		byte[] hash = new byte[HASH_LENGTH];

		Argon2BytesGenerator generator = new Argon2BytesGenerator();

		generator.init(parameters);
		generator.generateBytes(password.toCharArray(), hash);

		return hash;
	}
}