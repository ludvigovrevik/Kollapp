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

    private UserHandler userHandler = new UserHandler();

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
            controller.setUser(user);
            controller.populateGroupView();
            
            Scene scene = new Scene(parent);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Welcome " + user.getUsername() + "!");
            stage.show();

        } catch (IOException e) {
            System.out.println(e.getMessage());
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

    /**
     * Sets the {@link UserHandler} to manage user data. Primarily used for testing purposes.
     *
     * @param userHandler the user handler to be used
     */
    public void setUserHandler(UserHandler userHandler) {
        this.userHandler = userHandler;
    }
}
