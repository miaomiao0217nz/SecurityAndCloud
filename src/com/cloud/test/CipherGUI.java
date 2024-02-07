package com.cloud.test;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CipherGUI {

	public static Scene buildGUI() {

		TextArea inputTextArea = new TextArea();
		inputTextArea.setPromptText("Enter text");
		inputTextArea.setWrapText(true);
		inputTextArea.setPrefHeight(80);
		inputTextArea.setPrefWidth(400);

		TextField keyField = new TextField();
		keyField.setPrefHeight(30);
		keyField.setPromptText("Enter key");

		TextField configFileField = new TextField();
		configFileField.setPrefHeight(30);
		configFileField.setPromptText("choose cipher setting");

		TextArea outputArea = new TextArea();
		outputArea.setPrefHeight(80);
		outputArea.setPrefWidth(400);
		outputArea.setEditable(false);
		outputArea.setWrapText(true);
		outputArea.setPromptText("Cipher Text");

		ChoiceBox<String> cipherChoiceBox = new ChoiceBox<>();
		cipherChoiceBox.getItems().addAll(CAESAR_CIPHER, AES_CIPHER, DES_CIPHER);
		cipherChoiceBox.setValue(CAESAR_CIPHER);

		Button encryptButton = new Button("Encrypt");
		Button decryptButton = new Button("Decrypt");
		encryptButton.setPrefHeight(25);
		decryptButton.setPrefHeight(25);

		encryptButton.setOnAction(event -> {
			LoginPage.flashButton(encryptButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);
			String plainText = inputTextArea.getText();
			String key = keyField.getText();
			String selectedCipher = cipherChoiceBox.getValue();

			String cipherText = "Encryption failed!";
			try {
				if (selectedCipher.equals(CAESAR_CIPHER)) {

					try {
						int shift = Integer.parseInt(key);
						cipherText = CaesarCipher.encrypt(plainText, shift);
					} catch (NumberFormatException e) {
						LoginPage.showAlert(Alert.AlertType.ERROR, "Invalid Key",
								"For Caesar Cipher, the key must be an integer.");
						return;
					}
				}
				if (selectedCipher.equals(AES_CIPHER)) {

					cipherText = AES.encrypt(plainText, key.getBytes());
				} else if (selectedCipher.equals(DES_CIPHER)) {

					cipherText = DES.encrypt(plainText, key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputArea.setText(cipherText);
		});

		decryptButton.setOnAction(event -> {
			LoginPage.flashButton(decryptButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);
			String encryptedText = inputTextArea.getText();
			String key = keyField.getText();
			String selectedCipher = cipherChoiceBox.getValue();

			String plainText = "Decryption failed!";
			try {
				if (selectedCipher.equals(CAESAR_CIPHER)) {

					int shift = Integer.parseInt(key);
					plainText = CaesarCipher.decrypt(encryptedText, shift);
				}
				if (selectedCipher.equals(AES_CIPHER)) {

					plainText = AES.decrypt(encryptedText, key.getBytes());
				} else if (selectedCipher.equals(DES_CIPHER)) {
					plainText = DES.decrypt(encryptedText, key);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			outputArea.setText(plainText);
		});

		Button saveButton = new Button("Save Settings");
		Button loadButton = new Button("Load Settings");

		saveButton.setPrefHeight(25);
		loadButton.setPrefHeight(25);

		Button clearButton = new Button("Clear Input");
		clearButton.setPrefHeight(25);

		saveButton.setOnAction(event -> {
			LoginPage.flashButton(saveButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);
			String selectedAlgorithm = cipherChoiceBox.getValue();
			String userKey = keyField.getText();
			String algorithmName = configFileField.getText();

			try {
				String encryptedUserKey;
				if (selectedAlgorithm.equals(CAESAR_CIPHER)) {
					int shift;
					try {
						shift = Integer.parseInt(userKey);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						return;
					}
					System.out.println("Encrypted Key: " + shift);
					encryptedUserKey = AES.encrypt(String.valueOf(shift));
				} else {

					encryptedUserKey = AES.encrypt(userKey);
				}

				Database.saveSettings(selectedAlgorithm, encryptedUserKey, algorithmName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		loadButton.setOnAction(event -> {
			LoginPage.flashButton(loadButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);
			try {
				String algorithmname = configFileField.getText();
				String[] settings = Database.loadSettings(algorithmname);
				if (settings == null) {

					LoginPage.showAlert(Alert.AlertType.ERROR, "Load Error",
							"The specified algorithm settings could not be found.");
				} else if (settings.length == 4) {
					String algorithm = settings[1];
					String encryptedKey = settings[2];
					String decryptedUserKey = AES.decrypt(encryptedKey);

					if (algorithm.equals(CAESAR_CIPHER)) {
						try {
							String keyString = AES.decrypt(decryptedUserKey);
							int shift = Integer.parseInt(keyString);
							keyField.setText(String.valueOf(shift));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							System.out.print("Error in Caesar cipher key format.");
						}
					} else {
						keyField.setText(decryptedUserKey);
					}

					cipherChoiceBox.setValue(algorithm);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.print("Error in loading settings.");
			}
		});

		clearButton.setOnAction(event -> {
			LoginPage.flashButton(clearButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);

			inputTextArea.clear();
			keyField.clear();
			configFileField.clear();
			outputArea.clear();

		});

		VBox buttonBox = new VBox(30, cipherChoiceBox, encryptButton, decryptButton, saveButton, loadButton,
				clearButton);
		buttonBox.setAlignment(Pos.TOP_LEFT);

		VBox inputBox = new VBox(30, inputTextArea, keyField, outputArea, configFileField);
		inputBox.setAlignment(Pos.CENTER);

		HBox mainLayout = new HBox(20, buttonBox, inputBox);
		mainLayout.setAlignment(Pos.CENTER);
		mainLayout.setPadding(new Insets(20));
		mainLayout.setFillHeight(false);

		Image backgroundImage = new Image("/images/cipher.jpg");
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		mainLayout.setBackground(new Background(background));

		Scene scene = new Scene(mainLayout, 800, 500);
		return scene;
	}

	private static final String CAESAR_CIPHER = "Caesar Cipher";
	private static final String AES_CIPHER = "AES";
	private static final String DES_CIPHER = "DES";

}
