package com.cloud.test;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoginPage extends Application {
	private Scene cypherGUI = CipherGUI.buildGUI();

	public void start(Stage primaryStage) {
		GridPane loginGrid = new GridPane();
		loginGrid.setPadding(new Insets(18, 18, 18, 18));
		loginGrid.setVgap(15);
		loginGrid.setHgap(10);
		loginGrid.setAlignment(Pos.CENTER);
		Image backgroundImage = new Image("/images/cipher.jpg");
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
		loginGrid.setBackground(new Background(background));

		Label usernameLabel = new Label("UserName:");
		usernameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
		GridPane.setConstraints(usernameLabel, 0, 0);

		TextField usernameInput = new TextField();
		usernameInput.setPromptText("Please enter userName");
		usernameInput.setStyle("-fx-min-height: 40px; -fx-min-width: 240px;");
		GridPane.setConstraints(usernameInput, 1, 0);

		Label passwordLabel = new Label("Password:");
		passwordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
		GridPane.setConstraints(passwordLabel, 0, 1);

		PasswordField passwordInput = new PasswordField();
		passwordInput.setPromptText("please enter Password");
		passwordInput.setStyle("-fx-min-height: 40px; -fx-min-width: 240px;");
		GridPane.setConstraints(passwordInput, 1, 1);

		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-padding: 10px; -fx-min-width: 100px; -fx-text-fill: #0033AA;");

		Button signupButton = new Button("Sign Up");
		signupButton.setStyle("-fx-padding: 10px; -fx-min-width: 100px; -fx-text-fill: #0033AA;");

		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(loginButton, signupButton);
		GridPane.setConstraints(buttonBox, 1, 2);
		GridPane.setColumnSpan(buttonBox, 4);
		buttonBox.setPadding(new Insets(0, 30, 0, 0));

		loginGrid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, buttonBox);

		Scene loginScene = new Scene(loginGrid, 800, 500);

		loginButton.setOnAction(event -> {

			flashButton(loginButton, "-fx-padding: 10px;", "-fx-min-width: 100px;", "-fx-background-color: lightblue;",
					"-fx-background-color: #F0F0F0;", 1);

			String username = usernameInput.getText();
			String password = passwordInput.getText();
			boolean validLogin = false;
			try {
				validLogin = Database.validateLogin(username, salt -> hashPassword(password + salt));
			} catch (NoSuchAlgorithmException e) {

				e.printStackTrace();
			}
			if (validLogin) {
				showAlert(Alert.AlertType.INFORMATION, "Login Successful", "You have successfully logged in!");
				primaryStage.setScene(cypherGUI);
			} else {
				showAlert(Alert.AlertType.WARNING, "Login Failed", "Invalid username or password. Please try again.");
			}

			primaryStage.show();

		});

		Scene signupPage = SignUpPage.createSignUpPage(userName -> {
			usernameInput.setText(userName);
			primaryStage.setScene(loginScene);
			primaryStage.show();
		});

		signupButton.setOnAction(e -> {
			primaryStage.setScene(signupPage);
			primaryStage.show();
		});

		primaryStage.setScene(loginScene);

		primaryStage.show();
	}

	public static String hashPassword(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

	static void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public static void flashButton(Button button, String padding, String width, String flashColor, String defaultColor,
			double seconds) {
		String originalStyle = button.getStyle();
		button.setStyle(originalStyle + flashColor);
		PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
		pause.setOnFinished(event -> button.setStyle(originalStyle));
		pause.play();
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}

}
