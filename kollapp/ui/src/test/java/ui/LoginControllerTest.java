package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import client.UserApiHandler;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link LoginController} class.
 */
@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {
    
    private UserApiHandler mockUserHandler;

    // Headless mode is enabled
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
     * Sets up the test environment by loading the LoginScreen.fxml and initializing the controller.
     */
    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginScreen.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        mockUserHandler = Mockito.mock(UserApiHandler.class);
        controller.setUserApiHandler(mockUserHandler);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // You can set up any necessary preconditions here.
    }

    /**
     * Tests a successful login scenario where the user enters correct credentials.
     */
    @Test
    @DisplayName("Test successful login")
    @Tag("login")
    public void testSuccessfulLogin(FxRobot robot) {
        User TestUserDoNotDelete = new User("TestUserDoNotDelete", "password");

        when(mockUserHandler.userExists("TestUserDoNotDelete")).thenReturn(true);
        when(mockUserHandler.loadUser("TestUserDoNotDelete", "password")).thenReturn(TestUserDoNotDelete);

        robot.clickOn("#usernameField").write("TestUserDoNotDelete");
        robot.clickOn("#passwordField").write("password");
        robot.clickOn("Login");

        verify(mockUserHandler).userExists("TestUserDoNotDelete");
        verify(mockUserHandler).loadUser("TestUserDoNotDelete", "password");
    }

    /**
     * Tests the scenario where the user enters an incorrect password.
     */
    @Test
    @DisplayName("Test login with incorrect password")
    @Tag("login")
    public void testLoginIncorrectPassword(FxRobot robot) {
        when(mockUserHandler.userExists("TestUserDoNotDelete")).thenReturn(true);
        when(mockUserHandler.loadUser("TestUserDoNotDelete", "wrongPassword")).thenReturn(null);

        robot.clickOn("#usernameField").write("TestUserDoNotDelete");
        robot.clickOn("#passwordField").write("wrongPassword");
        robot.clickOn("#loginButton");

        verify(mockUserHandler).userExists("TestUserDoNotDelete");
        verify(mockUserHandler).loadUser("TestUserDoNotDelete", "wrongPassword");

        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("Incorrect password. Please try again.", loginErrorMessage.getText());
    }

    /**
     * Tests the scenario where the user tries to log in with a non-existent username.
     */
    @Test
    @DisplayName("Test login with non-existent user")
    @Tag("login")
    public void testLoginUserDoesNotExist(FxRobot robot) {
        when(mockUserHandler.userExists("nonTestUserDoNotDelete")).thenReturn(false);

        robot.clickOn("#usernameField").write("nonTestUserDoNotDelete");
        robot.clickOn("#passwordField").write("anyPassword");
        robot.clickOn("#loginButton");

        verify(mockUserHandler).userExists("nonTestUserDoNotDelete");

        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("No such user exists.", loginErrorMessage.getText());
    }

    /**
     * Tests the navigation from the login screen to the register screen.
     */
    @Test
    @DisplayName("Test navigation to register screen")
    @Tag("navigation")
    public void testNavigateToRegisterScreen(FxRobot robot) {
        robot.clickOn("#registerButton");

        Button navigateToLoginScreenButton = robot.lookup("#navigateToLoginScreenButton").queryAs(Button.class);
        assertEquals("Back", navigateToLoginScreenButton.getText());
    }
}
