package faceTag.mongo;

import java.math.BigInteger;
import java.security.SecureRandom;

public final class TokenGeneratorSingleton {
	private static TokenGeneratorSingleton instance;
	private static SecureRandom random;

	private TokenGeneratorSingleton() {
		random = new SecureRandom();
	}

	public static TokenGeneratorSingleton getInstance() {
		if (instance == null) {
			instance = new TokenGeneratorSingleton();
		}
		return instance;

	}

	public String generateToken() {
		return new BigInteger(130, random).toString(32);
	}
}