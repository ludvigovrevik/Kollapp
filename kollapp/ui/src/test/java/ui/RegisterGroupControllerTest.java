package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URL;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.UserHandler;

@ExtendWith(ApplicationExtension.class)
public class RegisterGroupControllerTest {

    private RegisterGroupController controller;
    private GroupHandler mockGroupHandler;
    private UserHandler mockUserHandler;
    private KollAppController mockKollAppController;
    private User mockUser;

    @Start
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/ui/RegisterGroup.fxml");
        assertNotNull(fxmlUrl, "FXML file not found! Check the resource path.");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Get the controller instance from the loader
        controller = loader.getController();

        // Mock dependencies
        mockGroupHandler = mock(GroupHandler.class);
        mockUserHandler = mock(UserHandler.class);
        mockKollAppController = mock(KollAppController.class);
        mockUser = mock(User.class);

        // Inject mocks into the controller using reflection
        setPrivateField(controller, "groupHandler", mockGroupHandler);
        setPrivateField(controller, "userHandler", mockUserHandler);
        setPrivateField(controller, "kollAppController", mockKollAppController);
        setPrivateField(controller, "user", mockUser);

        // Stub methods if necessary
        when(mockUser.getUsername()).thenReturn("testUser");

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp(FxRobot robot) {
        // Initialize any necessary state before each test
        // For this controller, initialization is done in the start method
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = RegisterGroupController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testCreateGroup_Success(FxRobot robot) throws Exception {
        // Arrange
        String groupName = "NewGroup";

        // Mock behavior for groupHandler.createGroup and userHandler.updateUser
        doNothing().when(mockGroupHandler).createGroup(mockUser, groupName);
        doNothing().when(mockUserHandler).updateUser(mockUser);

        // Act
        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group"); // Assuming the button text is unique

        // Assert
        // Verify that createGroup and updateUser were called
        verify(mockGroupHandler, times(1)).createGroup(mockUser, groupName);
        verify(mockUserHandler, times(1)).updateUser(mockUser);

        // Verify that populateGroupView was called on kollAppController
        verify(mockKollAppController, times(1)).populateGroupView();

        // Verify that the errorLabel shows success message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Made group succesfully", errorLabel.getText());
        assertEquals(javafx.scene.paint.Color.GREEN, errorLabel.getTextFill());
    }

    @Test
    void testCreateGroup_EmptyGroupName(FxRobot robot) throws Exception {
        // Arrange
        String groupName = "";

        // Act
        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group"); // Assuming the button text is unique

        // Assert
        // Verify that createGroup and updateUser were NOT called
        verify(mockGroupHandler, never()).createGroup(any(User.class), anyString());
        verify(mockUserHandler, never()).updateUser(any(User.class));

        // Verify that the errorLabel shows error message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Group Name cannot be empty", errorLabel.getText());
    }

    @Test
    void testCreateGroup_UserNotLoggedIn(FxRobot robot) throws Exception {
        // Arrange
        String groupName = "AnotherGroup";

        // Inject null user
        setPrivateField(controller, "user", null);

        // Act
        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group"); // Assuming the button text is unique

        // Assert
        // Verify that createGroup and updateUser were NOT called
        verify(mockGroupHandler, never()).createGroup(any(User.class), anyString());
        verify(mockUserHandler, never()).updateUser(any(User.class));

        // Verify that the errorLabel shows error message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("User not found. Please log in.", errorLabel.getText());
    }

    @Test
    void testCreateGroup_ExceptionDuringCreation(FxRobot robot) throws Exception {
        // Arrange
        String groupName = "ExceptionGroup";

        // Mock behavior to throw an exception when createGroup is called
        doThrow(new RuntimeException("Creation failed")).when(mockGroupHandler).createGroup(mockUser, groupName);

        // Act
        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group"); // Assuming the button text is unique

        // Assert
        // Verify that createGroup was called
        verify(mockGroupHandler, times(1)).createGroup(mockUser, groupName);
        // Verify that updateUser was NOT called due to exception
        verify(mockUserHandler, never()).updateUser(any(User.class));

        // Verify that populateGroupView was NOT called due to exception
        verify(mockKollAppController, never()).populateGroupView();

        // Verify that the errorLabel shows failure message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Failed to create group. Please try again.", errorLabel.getText());
    }
}