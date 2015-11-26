package com.citrus.citrususer;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RandomPassword implements PasswordGenerator{
	private MessageDigest md5;

	/**
	 * Creates a new pseudo random password generator.
	 */
	public RandomPassword() {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsax) {
			throw new RuntimeException(nsax);
		}
	}

	/**
	 * Generates a 8 character ([a-zA-Z]{8}) pseudo random password using the
	 * email as a seed.
	 * 
	 * As a consequence, same email will result in same password. Mobile number
	 * is ignored and can be null.
	 */
	@Override
	public String generate(String email, String mobile) {
		PseudoRandomNumberGenerator ran = new PseudoRandomNumberGenerator(
				generateSeed(email));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			builder.append(ran.nextLetter());
		}
		return builder.toString();
	}

	/**
	 * Generates a non-random positive number from a string.
	 * 
	 * @param data
	 *            the string to generate seed from.
	 * @return the value of the 3 highest bytes of the MD5 sum of data.
	 */
	public int generateSeed(String data) {
		byte hash[] = md5.digest(data.getBytes());
		md5.reset();
		hash = rangeCopy(hash, hash.length - 3, hash.length);
		return new BigInteger(1, hash).intValue();
	}

	public static byte[] rangeCopy(byte[] original, int from, int to) {
		int lengthNew = to - from;
		if (lengthNew < 0)
			throw new IllegalArgumentException(from + " > " + to);
		byte[] hash = new byte[lengthNew];
		System.arraycopy(original, from, hash, 0,
				Math.min(original.length - from, lengthNew));
		return hash;

	}

	/**
	 * A pseudo random integer generator.
	 */
	public static class PseudoRandomNumberGenerator {
		private int state;

		/**
		 * Creates a new pseudo random number generator.
		 * 
		 * @param seed
		 *            the seed to start the generation from.
		 */
		public PseudoRandomNumberGenerator(int seed) {
			this.state = seed;
		}

		/**
		 * Generates the next pseudo-random integer. The number is positive in
		 * the [0-max[ interval.
		 * 
		 * @param max
		 *            the exclusive upper boundary of the interval to generate
		 *            the pseudo-random in.
		 * @return the next number in the pseudo-random suite modulo max.
		 */
		public int nextInt(int max) {
			state = 7 * state % 3001;
			return (state - 1) % max;
		}

		/**
		 * Generates the next pseudo-random alphabetical character. The letter
		 * is in the [a-zA-Z] range.
		 * 
		 * @return the next letter in the pseudo-random suite.
		 */
		public char nextLetter() {
			int n = nextInt(52);
			return (char) (n + ((n < 26) ? 'A' : 'a' - 26));
		}

	}
}
