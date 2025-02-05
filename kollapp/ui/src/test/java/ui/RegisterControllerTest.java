package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

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

import client.UserApiHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link RegisterController} class.
 */
@ExtendWith(ApplicationExtension.class)
public class RegisterControllerTest extends ApplicationTest {

    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label errorMessage;
    private Button registerButton;
    private UserApiHandler userHandlerMock;
    private final UserApiHandler userApiHandler = new UserApiHandler();
    private RegisterController controller;
    private static final boolean headless = true;

    /**
     * Sets up the environment for headless mode if the 'headless' flag is true.
     */
    @BeforeAll
    private static void setupHeadlessMode() {
        if (headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    /**
     * Sets up the test environment by loading the RegisterScreen.fxml, initializing the scene, and injecting the mocked UserApiHandler.
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RegisterScreen.fxml"));
        Parent root = loader.load();

        controller = loader.getController();

        userHandlerMock = Mockito.mock(UserApiHandler.class);
        injectMockedUserApiHandler();

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void injectMockedUserApiHandler() throws Exception {
        Field userApiHandlerField = RegisterController.class.getDeclaredField("userApiHandler");
        userApiHandlerField.setAccessible(true);
        userApiHandlerField.set(controller, userHandlerMock);
    }

    @BeforeEach
    public void setUp() {
        usernameField = lookup("#usernameField").query();
        passwordField = lookup("#passwordField").query();
        confirmPasswordField = lookup("#confirmPasswordField").query();
        errorMessage = lookup("#errorMessage").query();
        registerButton = lookup("#registerButton").query();
    }

    /**
     * Tests the successful registration of a user with valid input.
     */
    @Test
    @DisplayName("Test successful user registration")
    @Tag("register")
    public void testRegisterUser_Success(FxRobot robot) {
        when(userHandlerMock.userExists("newUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("newUser", "password", "password")).thenReturn(true);

        robot.clickOn(usernameField).write("newUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("password");

        robot.clickOn(registerButton);

        // assertEquals("", errorMessage.getText());
    }

    /**
     * Tests user registration with mismatched passwords.
     */
    @Test
    @DisplayName("Test user registration with password mismatch")
    @Tag("register")
    public void testRegisterUser_PasswordMismatch(FxRobot robot) {
        when(userHandlerMock.userExists("testUser")).thenReturn(false);
        when(userHandlerMock.confirmNewValidUser("testUser", "password", "mismatch")).thenReturn(false);
        when(userHandlerMock.getUserValidationErrorMessage("testUser", "password", "mismatch")).thenReturn("Passwords do not match.");

        robot.clickOn(usernameField).write("testUser");
        robot.clickOn(passwordField).write("password");
        robot.clickOn(confirmPasswordField).write("mismatch");

        robot.clickOn(registerButton);

        assertEquals("Passwords do not match.", errorMessage.getText());
    }

    /**
     * Tests navigation from the register screen to the login screen.
     */
    @Test
    @DisplayName("Test navigation to login screen")
    @Tag("navigation")
    public void testNavigateToLoginScreen(FxRobot robot) {
        robot.clickOn("#navigateToLoginScreenButton");

        Button loginButton = robot.lookup("#loginButton").queryAs(Button.class);
        assertEquals("Login", loginButton.getText());
    }

    /**
     * Cleans up by removing the test user after each test.
     */
    @AfterEach
    @DisplayName("Remove test user after each test")
    public void removeUser() {
        userApiHandler.removeUser("newUser");
    }
}
