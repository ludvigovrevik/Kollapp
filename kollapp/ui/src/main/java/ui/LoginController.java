package ui;

import java.io.IOException;

import api.UserApiHandler;
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

/**
 * Controller class for handling user login and navigation to other scenes.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginErrorMessage;

    private UserApiHandler userApiHandler = new UserApiHandler();

    // Setter for injecting a mock during testing
    public void setUserApiHandler(UserApiHandler userApiHandler) {
        this.userApiHandler = userApiHandler;
    }

    /**
     * Handles the action event triggered by the login button.
     * Retrieves the username and password from the respective input fields,
     * checks if the user exists, and attempts to load the user.
     * If the user exists and the password is correct, it loads the Kollektiv scene.
     * Otherwise, it displays an appropriate error message.
     */
    @FXML
    public void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username == null || username.trim().isEmpty()) {
            loginErrorMessage.setText("Username cannot be empty.");
            return;
        }
        
        if (password == null || password.isEmpty()) {
            loginErrorMessage.setText("Password cannot be empty.");
            return;
        }

        if (userApiHandler.userExists(username)) {
            User user = userApiHandler.loadUser(username, password);
            if (user != null) {
                loadKollektivScene(user);
            } else {
                loginErrorMessage.setText("Incorrect password. Please try again.");
            }
        } else {
            loginErrorMessage.setText("No such user exists.");
        }
    }

    /**
     * Loads the Kollektiv scene and initializes the necessary controllers.
     *
     * @param user The user object containing user-specific data.
     */
    public void loadKollektivScene(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
            Parent parent = fxmlLoader.load();

            KollAppController controller = fxmlLoader.getController();
            controller.initializeToDoList(user); 
            controller.populateGroupView();
            
            Scene scene = new Scene(parent);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome " + user.getUsername() + "!");
            stage.show();

        } catch (IOException e) {
            loginErrorMessage.setText("Failed to load the next scene.");
        }
    }

    /**
     * Handles the action event triggered by the register button.
     * This method loads the RegisterScreen.fxml file and sets the scene to the new stage.
     *
     * @param event the action event triggered by the register button
     */

    @FXML
    public void handleRegisterButtonAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterScreen.fxml"));
        Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
}
