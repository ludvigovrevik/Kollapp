package ui;

import java.io.IOException;

import api.ToDoListApiHandler;
import api.UserApiHandler;
import core.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

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

    private final UserApiHandler userApiHandler = new UserApiHandler();

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
        
        if (!userApiHandler.userExists(username) && userApiHandler.confirmNewValidUser(username, password, confirmPassword)) {
            User user = new User(username, password);
            try {
                userApiHandler.saveUser(user); // Save user to JSON file
                ToDoListApiHandler handler = new ToDoListApiHandler();
                handler.assignToDoList(user); // Assign ToDo-list to JSON file
    
                switchToKollektivScene(event, user); // Switch to main screen
            } catch (IllegalArgumentException e) {
                errorMessage.setText("User creation failed: " + e.getMessage());
            } 
        } else {
            errorMessage.setText(userApiHandler.getUserValidationErrorMessage(username, password, confirmPassword));
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
            stage.setTitle("Login");

            // Center the stage on the screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            stage.show();
        } catch (IOException e) {
            errorMessage.setText("Failed to return to Login Screen.");
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
            controller.initializeToDoList(user);

            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome " + user.getUsername() + "!");

            // Center the stage on the screen
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
