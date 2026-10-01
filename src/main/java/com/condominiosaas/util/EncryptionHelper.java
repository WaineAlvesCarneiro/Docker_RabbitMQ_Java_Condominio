package com.condominiosaas.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
// Intentionally keep javax.crypto imports (do not migrate to jakarta.crypto)
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class EncryptionHelper {
	private static final String SECURITY_KEY = "C0nd0m1n10Cl0udS3cur1tyK3y_2026!"; // must match .NET key
	private static final int IV_LENGTH = 16;

	private EncryptionHelper() {}

	public static String decrypt(String cipherText) {
		if (cipherText == null || cipherText.isEmpty()) return cipherText;

		try {
			byte[] keyBytes = SECURITY_KEY.getBytes(StandardCharsets.UTF_8);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
			byte[] iv = new byte[IV_LENGTH]; // zero IV
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

			byte[] decoded = Base64.getDecoder().decode(cipherText);
			byte[] original = cipher.doFinal(decoded);
			return new String(original, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			// if decryption fails, return the original cipherText (same behavior as .NET helper)
			return cipherText;
		}

	}

	public static String encrypt(String plainText) {
		if (plainText == null || plainText.isEmpty()) return plainText;

		try {
			byte[] keyBytes = SECURITY_KEY.getBytes(StandardCharsets.UTF_8);
			SecretKeySpec keySpec = new SecretKeySpec(ensureKeyLength(keyBytes), "AES");
			byte[] iv = new byte[IV_LENGTH]; // zero IV
			IvParameterSpec ivSpec = new IvParameterSpec(iv);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			// if encryption fails, return the original text
			return plainText;
		}
	}

	private static byte[] ensureKeyLength(byte[] keyBytes) {
		// Ensure key is 32 bytes (AES-256) by trimming or padding with zeros
		byte[] k = new byte[32];
		int len = Math.min(keyBytes.length, k.length);
		System.arraycopy(keyBytes, 0, k, 0, len);
		return k;
	}

}
