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
import persistence.ToDoListHandler;
import persistence.UserHandler;
import core.User;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorMessage;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void navigateToLoginScreen(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registerUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (!UserHandler.userExists(username) && UserHandler.confirmNewValidUser(username, password, confirmPassword)) {
            User user = new User(username, password);
            UserHandler.saveUser(user);
            ToDoListHandler.assignToDoList(user);
            System.out.println("User registered successfully");
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
                Parent parent = fxmlLoader.load();
    
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage.setText(UserHandler.getUserValidationErrorMessage(username, password, confirmPassword));
        }
    }
}
