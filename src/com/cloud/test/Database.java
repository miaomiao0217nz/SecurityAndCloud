package com.cloud.test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;



public class Database {
	private static final String UserName = "admin";
	private static final String Password = "yBUT9trrdBOdI/KVuvujKQ=="; 

	public static Connection getConnection(String database) throws Exception {
		String url = "jdbc:mysql://securityandcloud.cm3qhsctmeql.ap-southeast-2.rds.amazonaws.com:3306/" + database
				+ "?user=admin";
		Properties props = new Properties();
		props.setProperty("user", UserName);
		props.setProperty("password", AES.decrypt(Password));
		return DriverManager.getConnection(url, props);
	}

	public static void saveSettings(String algorithm, String key, String algorithmName) throws Exception {
		String encryptedKey = AES.encrypt(key);
		try (Connection conn = getConnection("cipher_config");
				PreparedStatement pstmt = conn.prepareStatement(
						"INSERT INTO configuration (algorithm, encrypted_key,algorithmnames) VALUES (?,?,?)")) {
			pstmt.setString(1, algorithm);
			pstmt.setString(2, encryptedKey);
			pstmt.setString(3, algorithmName);
			pstmt.executeUpdate();
		}
	}

	public static String[] loadSettings(String algorithmname) throws Exception {
		try (Connection conn = getConnection("cipher_config");
				PreparedStatement pstmt = conn.prepareStatement(
						"SELECT  algorithmid, algorithm, encrypted_key, algorithmnames FROM configuration WHERE algorithmnames = ?")) {
			pstmt.setString(1, algorithmname);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String algorithmid = rs.getString("algorithmid");
				String algorithm = rs.getString("algorithm");
				String encryptedKey = rs.getString("encrypted_key");
				String algorithmName = rs.getString("algorithmnames");

				return new String[] { algorithmid, algorithm, encryptedKey, algorithmName };
			}
		}
		return null;
	}

	public static boolean validateLogin(String username, Function<String, String> saltedHasher)
			throws NoSuchAlgorithmException {

		try (Connection connection = Database.getConnection("users")) {

			String query = "SELECT salt, password FROM registration WHERE username = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, username);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				String storedHashPassword = resultSet.getString("password");
				String salt = resultSet.getString("salt");
				return Objects.equals(storedHashPassword, saltedHasher.apply(salt));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertIntoDatabase(String email, String username, String password) {

		try (Connection connection = getConnection("users")) {

			byte[] salt = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(salt);
			String saltString = Base64.getEncoder().encodeToString(salt);
			String hashedPassword = LoginPage.hashPassword(password + saltString);

			String checkQuery = "SELECT * FROM registration  WHERE username = ?";
			PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
			checkStatement.setString(1, username);
			ResultSet resultSet = checkStatement.executeQuery();

			if (!resultSet.next()) {
				String insertQuery = "INSERT INTO registration(email, username, password, salt) VALUES (?, ?, ?, ?)";
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

				preparedStatement.setString(1, email);
				preparedStatement.setString(2, username);
				preparedStatement.setString(3, hashedPassword);
				preparedStatement.setString(4, saltString);
				preparedStatement.executeUpdate();
				System.out.println("User registered successfully!");
				return true;

			} else {
				System.out.println("invalid registraiton!");
			}
			resultSet.close();
			checkStatement.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
