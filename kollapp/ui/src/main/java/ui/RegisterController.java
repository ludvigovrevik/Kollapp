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

/**
 * Controller class for the registration screen.
 * Handles user registration by validating input, saving new users,
 * and switching to the main application screen upon successful registration.
 */
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

    /**
     * Handles the registration process by validating the user's input.
     * If valid, the user is created and saved, and the application switches
     * to the main application screen. If invalid, appropriate error messages are shown.
     *
     * @param event The action event triggered by clicking the register button.
     */
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
    
                switchToKollektivScene(event, user); // Switch to main screen
            } catch (IllegalArgumentException e) {
                errorMessage.setText("User creation failed: " + e.getMessage());
            } catch (Exception e) {
                errorMessage.setText("An unexpected error occurred: " + e.getMessage());
            }
        } else {
            errorMessage.setText(userHandler.getUserValidationErrorMessage(username, password, confirmPassword));
        }
    }

    /**
     * Navigates back to the login screen when the user clicks the "Login" button.
     *
     * @param event The action event triggered by clicking the login button.
     */
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
    
    /**
     * Initialize the kollAppController with the user's new user and todo list, and switch 
     * to the main application screen.
     *
     * @param event The action event triggered by clicking the register button.
     * @param user  The newly registered user.
     */
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
}
