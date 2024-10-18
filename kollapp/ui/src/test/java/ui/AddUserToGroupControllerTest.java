package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.UserHandler;

/**
 * Unit tests for the AddUserToGroupController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class AddUserToGroupControllerTest {

    private AddUserToGroupController controller;
    private GroupHandler mockGroupHandler;
    private UserHandler mockUserHandler;
    private User mockUser;

    /**
     * Initializes the test environment by loading the AddUserToGroup.fxml and injecting mock dependencies.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    public void start(Stage stage) throws Exception {
        mockGroupHandler = mock(GroupHandler.class);
        mockUserHandler = mock(UserHandler.class);
        mockUser = mock(User.class);

        when(mockUser.getUserGroups()).thenReturn(Arrays.asList("Group1", "Group2"));

        controller = new AddUserToGroupController(mockUserHandler, mockGroupHandler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddUserToGroup.fxml"));
        loader.setControllerFactory(param -> controller);
        Parent root = loader.load();

        controller.initializeAddToUserGroup(mockUser);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Resets mocks before each test to ensure test isolation.
     *
     * @throws Exception if resetting fails
     */
    @BeforeEach
    public void setUp() throws Exception {
        reset(mockGroupHandler, mockUserHandler, mockUser);
    }

    /**
     * Tests adding a user to a group successfully.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding user to group successfully")
    @Tag("group")
    void testAddUserToGroup_Success(FxRobot robot) throws Exception {
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        // Set up mocks for successful user retrieval and group assignment
        User newUser = mock(User.class);
        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));
        doNothing().when(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        // Simulate user input and interaction
        robot.clickOn("#usernameField").write(inputUsername);
        
        @SuppressWarnings("unchecked")
        ListView<String> listView = robot.lookup("#groupsListView").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        robot.clickOn("Add user");

        // Verify interactions with mocks
        verify(mockUserHandler).userExists(inputUsername);
        verify(mockUserHandler).getUser(inputUsername);
        verify(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        // Assert feedback label
        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("User added to group successfully.", feedbackLabel.getText());
        assertEquals(javafx.scene.paint.Color.GREEN, feedbackLabel.getTextFill());
    }

    /**
     * Tests that adding a user with an empty username displays an appropriate error message.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding user with empty username")
    @Tag("group")
    void testAddUserToGroup_EmptyUsername(FxRobot robot) throws Exception {
        String inputUsername = "";
        String selectedGroup = "Group1";

        // Simulate user input and interaction
        robot.clickOn("#usernameField").write(inputUsername);
        
        @SuppressWarnings("unchecked")
        ListView<String> listView = robot.lookup("#groupsListView").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        robot.clickOn("Add user");

        // Verify that no interactions with handlers occur
        verify(mockUserHandler, never()).userExists(anyString());
        verify(mockUserHandler, never()).getUser(anyString());
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("Username is empty.", feedbackLabel.getText());
    }

    /**
     * Tests that adding a non-existent user displays an appropriate error message.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding non-existent user to group")
    @Tag("group")
    void testAddUserToGroup_UserDoesNotExist(FxRobot robot) throws Exception {
        String inputUsername = "nonExistingUser";
        String selectedGroup = "Group1";

        when(mockUserHandler.userExists(inputUsername)).thenReturn(false);

        robot.clickOn("#usernameField").write(inputUsername);
        
        @SuppressWarnings("unchecked")
        ListView<String> listView = robot.lookup("#groupsListView").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        robot.clickOn("Add user");

        verify(mockUserHandler).userExists(inputUsername);
        verify(mockUserHandler, never()).getUser(anyString());
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("User does not exist.", feedbackLabel.getText());
    }

    /**
     * Tests that adding a user without selecting a group displays an appropriate error message.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding user without selecting a group")
    @Tag("group")
    void testAddUserToGroup_NoGroupSelected(FxRobot robot) throws Exception {
        String inputUsername = "existingUser";

        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        User newUser = mock(User.class);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));

        robot.clickOn("#usernameField").write(inputUsername);
        robot.clickOn("Add user");

        verify(mockUserHandler).userExists(inputUsername);
        verify(mockUserHandler).getUser(inputUsername);
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("No group selected.", feedbackLabel.getText());
    }

    /**
     * Tests handling an exception during user assignment to a group.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test exception during user assignment to group")
    @Tag("group")
    void testAddUserToGroup_ExceptionDuringAssignment(FxRobot robot) throws Exception {
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        User newUser = mock(User.class);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.of(newUser));
        doThrow(new RuntimeException("Assignment failed")).when(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        robot.clickOn("#usernameField").write(inputUsername);
        
        @SuppressWarnings("unchecked")
        ListView<String> listView = robot.lookup("#groupsListView").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        robot.clickOn("Add user");

        verify(mockUserHandler).userExists(inputUsername);
        verify(mockUserHandler).getUser(inputUsername);
        verify(mockGroupHandler).assignUserToGroup(newUser, selectedGroup);

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("Failed to add user to group.", feedbackLabel.getText());
    }

    /**
     * Tests handling user retrieval failure.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test user retrieval failure during user assignment")
    @Tag("group")
    void testAddUserToGroup_UserRetrievalFailed(FxRobot robot) throws Exception {
        String inputUsername = "existingUser";
        String selectedGroup = "Group1";

        when(mockUserHandler.userExists(inputUsername)).thenReturn(true);
        when(mockUserHandler.getUser(inputUsername)).thenReturn(Optional.empty());

        robot.clickOn("#usernameField").write(inputUsername);
        
        @SuppressWarnings("unchecked")
        ListView<String> listView = robot.lookup("#groupsListView").queryAs(ListView.class);
        robot.interact(() -> listView.getSelectionModel().select(selectedGroup));
        robot.clickOn("Add user");

        verify(mockUserHandler).userExists(inputUsername);
        verify(mockUserHandler).getUser(inputUsername);
        verify(mockGroupHandler, never()).assignUserToGroup(any(User.class), anyString());

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel, "Feedback label not found!");
        assertEquals("User retrieval failed.", feedbackLabel.getText());
        assertEquals(javafx.scene.paint.Color.RED, feedbackLabel.getTextFill());
    }
}
