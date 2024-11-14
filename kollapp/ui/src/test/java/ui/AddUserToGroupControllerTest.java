package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import client.GroupApiHandler;
import client.UserApiHandler;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link AddUserToGroupController} class.
 */
@ExtendWith(ApplicationExtension.class)
public class AddUserToGroupControllerTest {

    private AddUserToGroupController controller;
    private UserApiHandler mockUserApiHandler;
    private GroupApiHandler mockGroupApiHandler;
    private User mockUser;

    // Headless mode is enabled
    static private boolean headless = true;

    /**
     * Sets up the environment for headless mode if the 'headless' flag is true.
     */
    @BeforeAll
    static void setupHeadlessMode() {
        if (headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    @Start
    public void start(Stage stage) throws Exception {
        mockGroupApiHandler = mock(GroupApiHandler.class);
        mockUserApiHandler = mock(UserApiHandler.class);
        mockUser = mock(User.class);

        when(mockUser.getUserGroups()).thenReturn(Arrays.asList("Group1", "Group2"));

        controller = new AddUserToGroupController();

        // Inject mock handlers
        controller.setUserApiHandler(mockUserApiHandler);
        controller.setGroupApiHandler(mockGroupApiHandler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddUserToGroup.fxml"));

        // Use setControllerFactory to supply the controller instance
        loader.setControllerFactory(param -> {
            if (param == AddUserToGroupController.class) {
                return controller;
            } else {
                // Default behavior for other controllers
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Parent root = loader.load();

        // Initialize the controller if necessary
        controller.initializeAddToUserGroup(mockUser);

        stage.setScene(new Scene(root));
        stage.show();
    }


    /**
     * Tests that adding a user without selecting a group displays an appropriate error message.
     */
    @Test
    @DisplayName("Test adding user without selecting a group")
    void testAddUserToGroup_NoGroupSelected(FxRobot robot) {
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);

        // Set username but do not select a group
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("No group selected.", feedbackLabel.getText());
    }

    /**
     * Tests that adding a user with an empty username displays an appropriate error message.
     */
    @Test
    @DisplayName("Test adding user with empty username")
    void testAddUserToGroup_EmptyUsername(FxRobot robot) {
        // Select a group but leave username empty
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("Username is empty.", feedbackLabel.getText());
    }

    /**
     * Tests that adding a user who does not exist displays an appropriate error message.
     */
    @Test
    @DisplayName("Test adding non-existent user")
    void testAddUserToGroup_UserDoesNotExist(FxRobot robot) {
        when(mockUserApiHandler.userExists("nonExistentUser")).thenReturn(false);

        // Set username and select a group
        robot.clickOn("#usernameField").write("nonExistentUser");
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("Failed to add user to group.", feedbackLabel.getText());
    }

    /**
     * Tests successful addition of a user to a group.
     */
    @Test
    @DisplayName("Test successful addition of user to group")
    void testAddUserToGroup_Success(FxRobot robot) {
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);
        when(mockGroupApiHandler.assignUserToGroup("existingUser", "Group1")).thenReturn(true);
        when(mockUserApiHandler.assignGroupToUser("existingUser", "Group1")).thenReturn(true);

        // Set username and select a group
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("User added successfully", feedbackLabel.getText());
    }

    /**
     * Tests failure in assigning user to group in GroupApiHandler.
     */
    @Test
    @DisplayName("Test failure in GroupApiHandler when adding user to group")
    void testAddUserToGroup_GroupApiHandlerFailure(FxRobot robot) {
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);
        when(mockGroupApiHandler.assignUserToGroup("existingUser", "Group1")).thenReturn(false);
        when(mockUserApiHandler.assignGroupToUser("existingUser", "Group1")).thenReturn(true);

        // Set username and select a group
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("Failed to add user to group.", feedbackLabel.getText());
    }

    /**
     * Tests failure in assigning group to user in UserApiHandler.
     */
    @Test
    @DisplayName("Test failure in UserApiHandler when adding group to user")
    void testAddUserToGroup_UserApiHandlerFailure(FxRobot robot) {
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);
        when(mockGroupApiHandler.assignUserToGroup("existingUser", "Group1")).thenReturn(true);
        when(mockUserApiHandler.assignGroupToUser("existingUser", "Group1")).thenReturn(false);

        // Set username and select a group
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("Failed to add user to group.", feedbackLabel.getText());
    }

    /**
     * Tests that an exception during the process displays an appropriate error message.
     */
    @Test
    @DisplayName("Test exception handling during adding user to group")
    void testAddUserToGroup_ExceptionHandling(FxRobot robot) {
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);
        when(mockGroupApiHandler.assignUserToGroup("existingUser", "Group1")).thenThrow(new RuntimeException("API Error"));

        // Set username and select a group
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("#groupsListView").clickOn("Group1");
        robot.clickOn("#addUserButton");

        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("Failed to add user to group: API Error", feedbackLabel.getText());
    }
}
