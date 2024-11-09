package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ui.api.UserApiHandler;

/**
 * Unit tests for the RegisterController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
class RegisterControllerIT extends ApplicationTest {

    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label errorMessage;
    private Button registerButton;

    private UserApiHandler userHandlerMock;
    private final UserApiHandler userApiHandler = new UserApiHandler();

     // Headless mode is enabled
    static private boolean headless = true;

    /**
     * Sets up the environment for headless mode if the 'headless' flag is true.
     * This method configures various system properties required for running
     * JavaFX tests in a headless environment.
     * 
     * Properties set:
     * - testfx.headless: Enables TestFX headless mode.
     */
    @BeforeAll
    static void setupHeadlessMode() {
        if(headless){
            System.setProperty("testfx.headless", "true");

            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    /**
     * Sets up the test environment by loading the RegisterScreen.fxml and initializing the scene.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RegisterScreen.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        usernameField = lookup("#usernameField").query();
        passwordField = lookup("#passwordField").query();
        confirmPasswordField = lookup("#confirmPasswordField").query();
        errorMessage = lookup("#errorMessage").query();
        registerButton = lookup("#registerButton").query();

        userHandlerMock = Mockito.mock(UserApiHandler.class);
    }

    /**
     * Tests the successful registration of a user with valid input.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test successful user registration")
    @Tag("register")
    void testRegisterUser_Success(FxRobot robot) {
        when(userHandlerMock.userExists("newUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("newUser", "password", "password")).thenReturn(true);

        robot.clickOn(usernameField).write("newUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("password");

        robot.clickOn(registerButton);

        assertEquals("", errorMessage.getText());
    }

    /**
     * Tests user registration with mismatched passwords.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test user registration with password mismatch")
    @Tag("register")
    void testRegisterUser_PasswordMismatch(FxRobot robot) {
        when(userHandlerMock.userExists("testUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("testUser", "password", "mismatch")).thenReturn(false);
        when(userHandlerMock.getUserValidationErrorMessage("testUser", "password", "mismatch")).thenReturn("Passwords do not match.");

        robot.clickOn(usernameField).write("testUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("mismatch");

        robot.clickOn(registerButton);

        assertEquals("Passwords do not match", errorMessage.getText());
    }

    /**
     * Tests navigation from the register screen to the login screen.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test navigation to login screen")
    @Tag("navigation")
    void testNavigateToLoginScreen(FxRobot robot) {
        robot.clickOn("#navigateToLoginScreenButton");

        Button loginButton = robot.lookup("#loginButton").queryAs(Button.class);
        assertEquals("Login", loginButton.getText());
    }

    /**
     * Cleans up by removing the test user after each test.
     */
    @AfterEach
    @DisplayName("Remove test user after each test")
    void removeUser() {
        userApiHandler.removeUser("newUser");
        // Remove user's todo list if required, not yet implemented in toDoListHandler.
    }
}
