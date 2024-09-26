package app;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private UserManager userManager;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // TODO load in all the users from the database and store them in a list
    public void initialize() {
        User ludvig = new User("ludvigho", "Ludvigho123");
        userManager = new UserManager();
        userManager.addUser(ludvig);
        System.out.println(userManager.getUser("ludvigho", "Ludvigho123").getUsername());
    }

    @FXML
    private void handleLoginButtonAction() throws Exception {
        System.out.println("Login button clicked!");
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println(username);
        System.out.println(password);

        User user = userManager.getUser(username, password);
        // System.out.println(user.getUsername());
        if (user != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Kollektiv.fxml"));
                Parent parent = fxmlLoader.load();

                KollAppController controller = fxmlLoader.getController();

                // TODO pass the user to the controller
                controller.setUser(user);
                Scene scene = new Scene(parent);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
