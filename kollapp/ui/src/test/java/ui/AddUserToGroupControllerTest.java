package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.UserHandler;

@ExtendWith(ApplicationExtension.class)
public class AddUserToGroupControllerTest {

    private AddUserToGroupController controller;
    private GroupHandler mockGroupHandler;
    private UserHandler mockUserHandler;
    private User mockUser;

    @Start
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/ui/AddUserToGroup.fxml");
        System.out.println("FXML URL: " + fxmlUrl);
        assertNotNull(fxmlUrl, "FXML file not found! Check the resource path.");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        // Get the controller instance from the loader
        controller = loader.getController();

        // Mock dependencies
        mockGroupHandler = mock(GroupHandler.class);
        mockUserHandler = mock(UserHandler.class);
        mockUser = mock(User.class);

        // Inject mocks into the controller using reflection
        setPrivateField(controller, "groupHandler", mockGroupHandler);
        setPrivateField(controller, "userHandler", mockUserHandler);
        setPrivateField(controller, "user", mockUser);

        // Mock user.getUserGroups() to return a list of groups
        when(mockUser.getUserGroups()).thenReturn(Arrays.asList("Group1", "Group2"));

        // Initialize the controller with mockUser
        controller.initializeAddToUserGroup(mockUser);

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Reset mocks before each test to ensure test isolation
        reset(mockGroupHandler, mockUserHandler, mockUser);
    }

    /**
     * Utility method to inject mock dependencies into private fields using reflection.
     *
     * @param target    The controller instance.
     * @param fieldName The name of the private field.
     * @param value     The mock object to set.
     * @throws Exception If the field does not exist or is inaccessible.
     */
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = AddUserToGroupController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Test adding a user to a group successfully.
     */
    @Test
    void testAddUserToGroup_Success(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        // Mock behavior for userHandler
        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        User newUser = mock(User.class);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));

        // Mock behavior for groupHandler
        doNothing().when(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        // Act
        // Enter username
        robot.clickOn("#username").write(inputUsername);
        // Select group from ListView
        ListView<String> listView = robot.lookup("#listViewOfGroups").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists and getUser were called
        verify(mockUserHandler, times(1)).userExists(inputUsername);
        verify(mockUserHandler, times(1)).getUser(inputUsername);

        // Verify that assignUserToGroup was called
        verify(mockGroupHandler, times(1)).assignUserToGroup(newUser, selectedGroup);

        // Verify that the errorLabel shows success message in green
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("User added to group successfully", errorLabel.getText());
        assertEquals(javafx.scene.paint.Color.GREEN, errorLabel.getTextFill());
    }

    /**
     * Test adding a user with an empty username.
     */
    @Test
    void testAddUserToGroup_EmptyUsername(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "";
        String selectedGroup = "Group1";

        // Act
        // Enter empty username
        robot.clickOn("#username").write(inputUsername);
        // Select group
        ListView<String> listView = robot.lookup("#listViewOfGroups").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists and getUser were NOT called
        verify(mockUserHandler, never()).userExists(anyString());
        verify(mockUserHandler, never()).getUser(anyString());
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        // Verify that the errorLabel shows "Username is empty"
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Username is empty", errorLabel.getText());
    }

    /**
     * Test adding a non-existent user to a group.
     */
    @Test
    void testAddUserToGroup_UserDoesNotExist(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "nonExistingUser";
        String selectedGroup = "Group1";

        // Mock behavior for userHandler
        when(mockUserHandler.userExists(inputUsername)).thenReturn(false);

        // Act
        // Enter username
        robot.clickOn("#username").write(inputUsername);
        // Select group
        ListView<String> listView = robot.lookup("#listViewOfGroups").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists was called
        verify(mockUserHandler, times(1)).userExists(inputUsername);
        // Verify that getUser and assignUserToGroup were NOT called
        verify(mockUserHandler, never()).getUser(anyString());
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        // Verify that the errorLabel shows "User does not exist"
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("User does not exist", errorLabel.getText());
    }

    /**
     * Test adding a user without selecting a group.
     */
    @Test
    void testAddUserToGroup_NoGroupSelected(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "existingUser";
        String selectedGroup = null; // No group selected

        // Mock behavior for userHandler
        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        User newUser = mock(User.class);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));

        // Act
        // Enter username
        robot.clickOn("#username").write(inputUsername);
        // Do not select any group
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists and getUser were called
        verify(mockUserHandler, times(1)).userExists(inputUsername);
        verify(mockUserHandler, times(1)).getUser(inputUsername);
        // Verify that assignUserToGroup was NOT called
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        // Verify that the errorLabel shows "No group selected"
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("No group selected", errorLabel.getText());
    }

    /**
     * Test handling an exception during user assignment to a group.
     */
    @Test
    void testAddUserToGroup_ExceptionDuringAssignment(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        // Mock behavior for userHandler
        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        User newUser = mock(User.class);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));

        // Mock behavior for groupHandler to throw an exception
        doThrow(new RuntimeException("Assignment failed")).when(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        // Act
        // Enter username
        robot.clickOn("#username").write(inputUsername);
        // Select group
        ListView<String> listView = robot.lookup("#listViewOfGroups").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists and getUser were called
        verify(mockUserHandler, times(1)).userExists(inputUsername);
        verify(mockUserHandler, times(1)).getUser(inputUsername);
        // Verify that assignUserToGroup was called and threw an exception
        verify(mockGroupHandler, times(1)).assignUserToGroup(newUser, selectedGroup);

        // Verify that the errorLabel shows "Failed to add user to group"
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Failed to add user to group", errorLabel.getText());
    }

    /**
     * Test handling user retrieval failure.
     */
    @Test
    void testAddUserToGroup_UserRetrievalFailed(FxRobot robot) throws Exception {
        // Arrange
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        // Mock behavior for userHandler
        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.empty());

        // Act
        // Enter username
        robot.clickOn("#username").write(inputUsername);
        // Select group
        ListView<String> listView = robot.lookup("#listViewOfGroups").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        // Click "Add user" button
        robot.clickOn("Add user"); // Assuming the button text is unique

        // Assert
        // Verify that userExists and getUser were called
        verify(mockUserHandler, times(1)).userExists(inputUsername);
        verify(mockUserHandler, times(1)).getUser(inputUsername);
        // Verify that assignUserToGroup was NOT called
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        // Verify that the errorLabel shows "User retrieval failed"
        Label errorLabel = robot.lookup("#userNameErrorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("User retrieval failed", errorLabel.getText());
    }
}