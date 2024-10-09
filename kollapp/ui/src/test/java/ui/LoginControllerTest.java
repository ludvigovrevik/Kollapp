package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
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
    private UserHandler userHandler = new UserHandler(); // Instance of UserHandler to be used for real


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
        // Make sure the existingUser exists and the password is correct
        
    }

    @Test
    void testSuccessfulLogin(FxRobot robot) throws Exception {
        User existingUser = new User("existingUser", "password");
        // Stub methods on the mockUserHandler
        when(mockUserHandler.userExists("existingUser")).thenReturn(true);
        
        when(mockUserHandler.loadUser("existingUser", "password")).thenReturn(existingUser);

        // Simulate user input
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#passwordField").write("password");

        // Click the login button
        robot.clickOn("#loginButton"); // Ensure the login button has fx:id="loginButton"

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("existingUser");
        verify(mockUserHandler).loadUser("existingUser", "password");

        // Verify that the loginErrorMessage is empty
        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("", loginErrorMessage.getText());

    }

    @Test
    void testLoginIncorrectPassword(FxRobot robot) throws Exception {
        // Stub methods to simulate incorrect password
        when(mockUserHandler.userExists("existingUser")).thenReturn(true);
        when(mockUserHandler.loadUser("existingUser", "wrongPassword")).thenReturn(null);

        // Simulate user input
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#passwordField").write("wrongPassword");

        // Click the login button
        robot.clickOn("#loginButton");

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("existingUser");
        verify(mockUserHandler).loadUser("existingUser", "wrongPassword");

        // Verify that the correct error message is displayed
        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("Incorrect password. Please try again.", loginErrorMessage.getText());
    }

    @Test
    void testLoginUserDoesNotExist(FxRobot robot) throws Exception {
        // Stub methods to simulate user does not exist
        when(mockUserHandler.userExists("nonExistingUser")).thenReturn(false);

        // Simulate user input
        robot.clickOn("#usernameField").write("nonExistingUser");
        robot.clickOn("#passwordField").write("anyPassword");

        // Click the login button
        robot.clickOn("#loginButton");

        // Verify interactions with the mock
        verify(mockUserHandler).userExists("nonExistingUser");

        // Verify that the correct error message is displayed
        Label loginErrorMessage = robot.lookup("#loginErrorMessage").queryAs(Label.class);
        assertEquals("No such user exists.", loginErrorMessage.getText());
    }

    @Test
    void testNavigateToRegisterScreen(FxRobot robot) throws Exception {
        // Click the register button
        robot.clickOn("#registerButton"); // Ensure the register button has fx:id="registerButton"

        // Verify that the register screen is displayed
        // For example, check if a component unique to the register screen is present
        Button navigateToLoginScreenButton = robot.lookup("#navigateToLoginScreenButton").queryAs(Button.class);
        assertEquals("Back", navigateToLoginScreenButton.getText());
    }

}