package ui;

import java.io.IOException;

import core.User;
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

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorMessage;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private UserHandler userHandler = new UserHandler();

    @FXML
    private void registerUser(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (!userHandler.userExists(username) && userHandler.confirmNewValidUser(username, password, confirmPassword)) {
            User user = new User(username, password);
            try {
                userHandler.saveUser(user); // Save user to JSON file
                ToDoListHandler handler = new ToDoListHandler();
                handler.assignToDoList(user); // Assign ToDo-list to JSON file
    
                switchToKollektivScene(event, user);
            } catch (IllegalArgumentException e) {
                errorMessage.setText("User creation failed: " + e.getMessage());
            } catch (Exception e) {
                errorMessage.setText("An unexpected error occurred: " + e.getMessage());
            }
        } else {
            errorMessage.setText(userHandler.getUserValidationErrorMessage(username, password, confirmPassword));
        }
    }

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
    
    private void switchToKollektivScene(ActionEvent event, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
            Parent parent = fxmlLoader.load();

            KollAppController controller = fxmlLoader.getController();
            controller.innitializeToDoList(user);

            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserHandler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }
    
}
