package ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.UserHandler;
import core.User;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginErrorMessage;

    @FXML
    private void handleLoginButtonAction() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        UserHandler userHandler = new UserHandler();

        if (userHandler.userExists(username)) {
            User user = userHandler.loadUser(username, password);

            if (user != null) {
                loadKollektivScene(user);
            } else {
                loginErrorMessage.setText("Incorrect password. Please try again.");
            }
        } else {
            loginErrorMessage.setText("No such user exists.");
        }
    }

    private void loadKollektivScene(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
            Parent parent = fxmlLoader.load();

            KollAppController controller = fxmlLoader.getController();
            controller.innitializeToDoList(user); 

            Scene scene = new Scene(parent);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            loginErrorMessage.setText("Failed to load the next scene.");
        }
    }

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterScreen.fxml"));
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
