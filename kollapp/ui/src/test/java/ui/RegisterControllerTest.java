package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

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

    private UserHandler userHandlerMock;  // Instance of UserHandler to be mocked
    private UserHandler userHandler = new UserHandler(); // Instance of UserHandler to be used for real

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

        // Initialize the mock instance of UserHandler
        userHandlerMock = Mockito.mock(UserHandler.class);
        
    }

    @Test
    void testRegisterUser_Success(FxRobot robot) {
        // Mock the behavior of userHandler methods to simulate a successful registration
        when(userHandlerMock.userExists("newUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("newUser", "password", "password")).thenReturn(true);

        // Simulate filling out the form
        robot.clickOn(usernameField).write("newUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("password");

        // Simulate clicking the register button
        robot.clickOn(registerButton);

        // Verify that no error message is set (i.e., user registration is successful)
        assertEquals("", errorMessage.getText());

    }

    @Test
    void testRegisterUser_PasswordMismatch(FxRobot robot) {
        // Mock the behavior of userHandler to simulate a password mismatch
        when(userHandlerMock.userExists("testUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("testUser", "password", "mismatch")).thenReturn(false);
        when(userHandlerMock.getUserValidationErrorMessage("testUser", "password", "mismatch")).thenReturn("Passwords do not match.");

        // Simulate filling out the form with mismatching passwords
        robot.clickOn(usernameField).write("testUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("mismatch");

        // Simulate clicking the register button
        robot.clickOn(registerButton);

        // Verify that the error message is displayed
        assertEquals("Passwords do not match", errorMessage.getText());
    }

    @Test
    void testNavigateToLoginScreen(FxRobot robot) throws Exception {
        // Click the register button
        robot.clickOn("#navigateToLoginScreenButton");

        // Verify that the register screen is displayed
        Button loginButton = robot.lookup("#loginButton").queryAs(Button.class);
        assertEquals("Login", loginButton.getText());
    }

    @AfterEach
    void removeUser() {
        // Remove the user from the JSON file
        userHandler.removeUser("newUser");
    }

}
