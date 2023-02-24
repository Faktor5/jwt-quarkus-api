package org.wtaa.service;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class Encoder {

	@ConfigProperty(name = "org.wtaa.password.secret")
	String secret;
	@ConfigProperty(name = "org.wtaa.password.iteration")
	Integer iteration;
	@ConfigProperty(name = "org.wtaa.password.keylength")
	Integer keylength;

	public Encoder() {
		System.out.println("Encoder created"
				+ " secret: " + secret
				+ " iteration: " + iteration
				+ " keylength: " + keylength);
	}

	/**
	 * More info (https://www.owasp.org/index.php/Hashing_Java) 404 :(
	 * 
	 * @param cs password
	 * @return encoded password
	 */
	public String encodeBase64(CharSequence cs) {
		try {
			byte[] result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
					.generateSecret(
							new PBEKeySpec(cs.toString().toCharArray(), secret.getBytes(), iteration, keylength))
					.getEncoded();
			return Base64.getEncoder().encodeToString(result);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			throw new RuntimeException(ex);
		}
	}

	public String decodeToken(CharSequence cs) {

		return List.of(cs.toString().split("\\.")).stream()
				.map(s -> new String(Base64.getUrlDecoder().decode(s), StandardCharsets.UTF_8))
				.reduce((s1, s2) -> s1 + "." + s2)
				.orElseThrow();

	}

}