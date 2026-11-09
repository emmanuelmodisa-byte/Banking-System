package bank.view;

import bank.controller.LoginController;
import bank.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

//The login screen. Only bank staff (Teller and Manager) can log in
public class LoginView {
    private VBox root = new VBox(10);

    public LoginView(LoginController controller, Consumer<User> onLoginSuccess) {
        Label title = new Label("Staff-Login");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #A51F3D;");
        Label subtitle = new Label("Banking Management System");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        Button loginButton = new Button("Login");
        Label messageLabel = new Label();

        loginButton.setOnAction(e -> {
            boolean ok = controller.login(usernameField.getText(), passwordField.getText());
            if (ok) {
                passwordField.clear();
                messageLabel.setText("");
                onLoginSuccess.accept(controller.getLoggedInUser());
            } else {
                ViewUtil.showMessage(messageLabel, controller.getMessage(), false);
            }
        });

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getChildren().addAll(title, subtitle, usernameField, passwordField, loginButton, messageLabel);
    }

    public VBox getRoot() { return root; }
}
