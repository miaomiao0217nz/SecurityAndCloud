package com.cloud.test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class AES {

	private static final int AES_KEY_SIZE = 16;
	private static final String KEY_FILE = "masterkey.txt";
	private static final byte[] key_bytes;
	static {
		try {
			key_bytes = readMasterKey();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String encryptedText) throws Exception {
		return decrypt(encryptedText, key_bytes);
	}

	public static String encrypt(String plainText) throws Exception {
		return encrypt(plainText, key_bytes);
	}

	public static String encrypt(String plainText, byte[] key) throws Exception {

		byte[] keyBytes = new byte[AES_KEY_SIZE];
		System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, AES_KEY_SIZE));
		SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	public static String decrypt(String encryptedText, byte[] key) throws Exception {
		byte[] keyBytes = new byte[AES_KEY_SIZE];
		System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, AES_KEY_SIZE));

		SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	private static byte[] readMasterKey() throws IOException {
		return Files.readAllBytes(Paths.get(KEY_FILE));
	}

}
