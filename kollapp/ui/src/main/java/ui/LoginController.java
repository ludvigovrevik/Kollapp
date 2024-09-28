package ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private void handleLoginButtonAction() throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (UserHandler.userExists(username)) {
            User user = UserHandler.loadUser(username, password);
            if (user != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
                    Parent parent = fxmlLoader.load();

                    KollAppController controller = fxmlLoader.getController();

                    // TODO pass the user to the controller
                    controller.setUser(user); // TODO implement setUser method

                    Scene scene = new Scene(parent);
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No such user exists.");
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
