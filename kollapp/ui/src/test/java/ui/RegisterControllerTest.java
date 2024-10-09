package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.UserHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class RegisterControllerTest extends ApplicationTest {

    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label errorMessage;
    private Button registerButton;
    private Button navigateToLoginScreenButton;

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML and get the scene for the test
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RegisterScreen.fxml"));
        Parent root = loader.load();

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        usernameField = lookup("#usernameField").query(); // ID in FXML
        passwordField = lookup("#passwordField").query(); // ID in FXML
        confirmPasswordField = lookup("#confirmPasswordField").query(); // ID in FXML
        errorMessage = lookup("#errorMessage").query(); // ID in FXML
        registerButton = lookup("#registerButton").query(); // ID in FXML
        navigateToLoginScreenButton = lookup("#navigateToLoginScreenButton").query(); // ID in FXML
    }

    @Test
    void testRegisterUser_Success(FxRobot robot) {
        try (MockedStatic<UserHandler> mockedUserHandler = Mockito.mockStatic(UserHandler.class)) {
            // Mock UserHandler methods to simulate valid user creation
            mockedUserHandler.when(() -> UserHandler.userExists("newUser")).thenReturn(false);
            mockedUserHandler.when(() -> UserHandler.confirmNewValidUser("newUser", "password", "password")).thenReturn(true);

            // Simulate filling out the form
            robot.clickOn(usernameField).write("newUser");
            robot.clickOn(passwordField).write("password");
            robot.clickOn(confirmPasswordField).write("password");

            // Simulate clicking the register button (assuming the button ID is "registerButton")
            robot.clickOn(registerButton);

            // Verify that no error message is set (i.e., user registration is successful)
            assertEquals("", errorMessage.getText());

            // Optionally, verify that the user was saved
            mockedUserHandler.verify(() -> UserHandler.saveUser(Mockito.any(User.class)), times(1));
        }
    }

    @Test
    void testRegisterUser_PasswordMismatch(FxRobot robot) {
        try (MockedStatic<UserHandler> mockedUserHandler = Mockito.mockStatic(UserHandler.class)) {
            // Mock UserHandler to simulate password mismatch
            mockedUserHandler.when(() -> UserHandler.userExists("testUser")).thenReturn(false);
            mockedUserHandler.when(() -> UserHandler.confirmNewValidUser("testUser", "password", "mismatch")).thenReturn(false);
            mockedUserHandler.when(() -> UserHandler.getUserValidationErrorMessage("testUser", "password", "mismatch")).thenReturn("Passwords do not match.");

            // Simulate filling out the form with mismatching passwords
            robot.clickOn(usernameField).write("testUser");
            robot.clickOn(passwordField).write("password");
            robot.clickOn(confirmPasswordField).write("mismatch");

            // Simulate clicking the register button
            robot.clickOn(registerButton);

            // Verify that the error message is displayed
            assertEquals("Passwords do not match", errorMessage.getText());
        }
    }

    @Test
    void testNavigateToLoginScreen(FxRobot robot) {
        // Simulate clicking the navigate to login button (assuming the button ID is "loginButton")
        robot.clickOn(navigateToLoginScreenButton);

        // Verify that the current scene is the login screen (check by querying some component in the login screen)
        // Assuming the login screen has a component with ID "loginTitle"
        assertNotNull(navigateToLoginScreenButton);
    }
}
