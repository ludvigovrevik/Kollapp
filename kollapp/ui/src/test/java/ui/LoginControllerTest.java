package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import persistence.UserHandler;

@ExtendWith(ApplicationExtension.class)
public class LoginControllerTest {

    private LoginController controller;
    private UserHandler mockUserHandler;

    @Start
    public void start(Stage stage) throws Exception {
        // Load the FXML and get the scene for the test
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginScreen.fxml"));
        Parent root = loader.load();
        // Get the controller instance from the loader
        controller = loader.getController();
        // Initialize the mock and inject it into the controller
        mockUserHandler = Mockito.mock(UserHandler.class);
        controller.setUserHandler(mockUserHandler);
        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {

    }

    @Test
    void testSuccessfulLogin(FxRobot robot) throws Exception {
        User TestUserDoNotDelete = new User("TestUserDoNotDelete", "password");

        // Stub methods on the mockUserHandler
        when(mockUserHandler.userExists("TestUserDoNotDelete")).thenReturn(true);
        when(mockUserHandler.loadUser("TestUserDoNotDelete", "password")).thenReturn(TestUserDoNotDelete);

        // Simulate user input
        robot.clickOn("#usernameField").write("TestUserDoNotDelete");
        robot.clickOn("#passwordField").write("password");

        // Click the login button
        robot.clickOn("#loginButton");

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("TestUserDoNotDelete");
        verify(mockUserHandler).loadUser("TestUserDoNotDelete", "password");
    }


    @Test
    void testLoginIncorrectPassword(FxRobot robot) throws Exception {
        // Stub methods to simulate incorrect password
        when(mockUserHandler.userExists("TestUserDoNotDelete")).thenReturn(true);
        when(mockUserHandler.loadUser("TestUserDoNotDelete", "wrongPassword")).thenReturn(null);

        // Simulate user input
        robot.clickOn("#usernameField").write("TestUserDoNotDelete");
        robot.clickOn("#passwordField").write("wrongPassword");

        // Click the login button
        robot.clickOn("#loginButton");

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("TestUserDoNotDelete");
        verify(mockUserHandler).loadUser("TestUserDoNotDelete", "wrongPassword");

        // Verify that the correct error message is displayed
        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("Incorrect password. Please try again.", loginErrorMessage.getText());
    }

    @Test
    void testLoginUserDoesNotExist(FxRobot robot) throws Exception {
        // Stub methods to simulate user does not exist
        when(mockUserHandler.userExists("nonTestUserDoNotDelete")).thenReturn(false);

        // Simulate user input
        robot.clickOn("#usernameField").write("nonTestUserDoNotDelete");
        robot.clickOn("#passwordField").write("anyPassword");

        // Click the login button
        robot.clickOn("#loginButton");

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("nonTestUserDoNotDelete");

        // Verify that the correct error message is displayed
        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("No such user exists.", loginErrorMessage.getText());
    }

    @Test
    void testNavigateToRegisterScreen(FxRobot robot) throws Exception {
        // Click the register button
        robot.clickOn("#registerButton");

        // Verify that the register screen is displayed
        Button navigateToLoginScreenButton = robot.lookup("#navigateToLoginScreenButton").queryAs(Button.class);
        assertEquals("Back", navigateToLoginScreenButton.getText());
    }
}
