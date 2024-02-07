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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class SignUpPage {

	public static Scene createSignUpPage(Consumer<String> callback) {
		GridPane signUpGrid = new GridPane();
		signUpGrid.setPadding(new Insets(20, 20, 20, 20));
		signUpGrid.setVgap(15);
		signUpGrid.setHgap(10);
		signUpGrid.setAlignment(Pos.CENTER);
		Image backgroundImage = new Image("/images/cipher.jpg");
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		signUpGrid.setBackground(new Background(background));

		Label signUpLabel = new Label("Sign Up:");
		signUpLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
		signUpLabel.setPadding(new Insets(0, 0, 0, 75));
		GridPane.setConstraints(signUpLabel, 1, 0);

		TextField emailInput = new TextField();
		emailInput.setPromptText("please enter your email");
		emailInput.setStyle("-fx-min-height: 40px; -fx-min-width: 240px;");
		GridPane.setConstraints(emailInput, 1, 1);

		TextField userNameInput = new TextField();
		userNameInput.setPromptText("please enter your username");
		userNameInput.setStyle("-fx-min-height: 40px; -fx-min-width: 240px;");
		GridPane.setConstraints(userNameInput, 1, 2);

		PasswordField signUpPasswordInput = new PasswordField();
		signUpPasswordInput.setPromptText("please enter your Password");
		signUpPasswordInput.setStyle("-fx-min-height: 40px; -fx-min-width: 240px;");
		GridPane.setConstraints(signUpPasswordInput, 1, 3);

		Button registerButton = new Button("Register");
		registerButton.setStyle("-fx-padding: 10px; -fx-min-width: 100px; -fx-text-fill: #0033AA;");
		HBox Box = new HBox();
		Box.setAlignment(Pos.CENTER);
		Box.getChildren().addAll(registerButton);
		GridPane.setConstraints(Box, 1, 5);

		signUpGrid.getChildren().addAll(signUpLabel, emailInput, userNameInput, signUpPasswordInput, Box);

		Scene signUpScene = new Scene(signUpGrid, 800, 500);

		registerButton.setOnAction(event -> {
			LoginPage.flashButton(registerButton, "-fx-padding: 10px;", "-fx-min-width: 100px;",
					"-fx-background-color: lightblue;", "-fx-background-color: #F0F0F0;", 1);

			String email = emailInput.getText();
			String username = userNameInput.getText();
			String password = signUpPasswordInput.getText();
			boolean isValid = true;

			if (!isValidEmail(email)) {
				showAlert(Alert.AlertType.WARNING, "Invalid Email", "Please enter a valid email address.");
				isValid = false;
			}
			if (!isValidPassword(password)) {
				showAlert(Alert.AlertType.WARNING, "Invalid Password",
						"Password must contain at least one number, one uppercase and one lowercase letter, and at least 8 or more characters.");
				isValid = false;
			}
			if (!isValidUsername(username)) {
				showAlert(Alert.AlertType.WARNING, "Invalid Username",
						"Username must be 6-20 characters long and can only contain letters, numbers, and underscores.");
				isValid = false;
			}

			if (isValid) {
				boolean inserted = Database.insertIntoDatabase(email, username, password);
				if (inserted) {
					showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
					callback.accept(username);
				} else {
					showAlert(Alert.AlertType.WARNING, "Registration Failed",
							"Username or email already exists or other error occurred.");
					
				}
			}

		});

		return signUpScene;
	}


	public static boolean isValidUsername(String username) {
		String usernameRegex = "^[a-zA-Z0-9_]{6,20}$";

		Pattern pattern = Pattern.compile(usernameRegex);
		if (username == null) {
			return false;
		}
		return pattern.matcher(username).matches();
	}

	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		if (email == null) {
			return false;
		}
		return pattern.matcher(email).matches();
	}

	public static boolean isValidPassword(String password) {
		String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

		Pattern pattern = Pattern.compile(passwordRegex);
		if (password == null) {
			return false;
		}
		return pattern.matcher(password).matches();
	}

	private static void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
