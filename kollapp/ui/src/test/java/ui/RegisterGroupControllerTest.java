package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import ui.api.GroupApiHandler;
import ui.api.UserApiHandler;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Unit tests for the RegisterGroupController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class RegisterGroupControllerTest {

    private RegisterGroupController controller;
    private GroupApiHandler mockGroupApiHandler;
    private UserApiHandler mockUserApiHandler;
    private KollAppController mockKollAppController;
    private User mockUser;
    private String mockUsername;

    /**
     * Initializes the test environment by loading the RegisterGroup.fxml and setting up dependencies.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    public void start(Stage stage) throws Exception {
            URL fxmlUrl = getClass().getResource("/ui/RegisterGroup.fxml");
            assertNotNull(fxmlUrl, "FXML file not found! Check the resource path.");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            controller = loader.getController();

            mockGroupApiHandler = mock(GroupApiHandler.class);
            mockKollAppController = mock(KollAppController.class);
            mockUserApiHandler = mock(UserApiHandler.class);
            mockUser = mock(User.class);
            mockUsername = "testUser";

            setPrivateField(controller, "groupApiHandler", mockGroupApiHandler);
            setPrivateField(controller, "kollAppController", mockKollAppController);
            setPrivateField(controller, "user", mockUser);

            when(mockUser.getUsername()).thenReturn("testUser");

            controller.setKollAppController(mockKollAppController);
            controller.setGroupApiHandler(mockGroupApiHandler);
            controller.setUserApiHandler(mockUserApiHandler);

            stage.setScene(new Scene(root));
            stage.show();
    }

    @BeforeEach
    public void setUp(FxRobot robot) {
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockGroupApiHandler.createGroup("testUser", "NewGroup")).thenReturn(true);
        when(mockUserApiHandler.assignGroupToUser("testUser", "NewGroup")).thenReturn(true);

        // Set the user in the controller
        controller.setUser(mockUser);
    }

    /**
     * Sets the value of a private field in the specified target object.
     *
     * @param target the object whose private field is to be modified
     * @param fieldName the name of the private field to be modified
     * @param value the new value to set for the private field
     * @throws Exception if the field cannot be found or accessed
     */
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = RegisterGroupController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Tests successful group creation when valid inputs are provided.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test successful group creation")
    @Tag("group")
    void testCreateGroup_Success(FxRobot robot) throws Exception {
        String groupName = "NewGroup";
        List<String> mockGroups = List.of("Group1", "Group2", groupName);

        when(mockGroupApiHandler.createGroup(mockUser.getUsername(), groupName)).thenReturn(true);
        when(mockUser.getUserGroups()).thenReturn(mockGroups);

        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group");

        verify(mockGroupApiHandler, times(1)).createGroup(mockUser.getUsername(), groupName);
        verify(mockKollAppController, times(1)).populateGroupView(mockGroups);
    }

    /**
     * Tests that the group creation fails if the group name is empty.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test group creation with empty group name")
    @Tag("group")
    void testCreateGroup_EmptyGroupName(FxRobot robot) throws Exception {
        String groupName = "";

        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group");

        verify(mockGroupApiHandler, never()).createGroup(anyString(), anyString());

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Group Name cannot be empty", errorLabel.getText());
    }

    /**
     * Tests that an appropriate error message is shown when an exception is thrown during group creation.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test group creation exception")
    @Tag("group")
    void testCreateGroup_ExceptionDuringCreation(FxRobot robot) throws Exception {
        String groupName = "ExceptionGroup";

        doThrow(new RuntimeException("Creation failed")).when(mockGroupApiHandler).createGroup(mockUsername, groupName);

        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group");

        verify(mockGroupApiHandler, times(1)).createGroup(mockUsername, groupName);
        verify(mockKollAppController, never()).populateGroupView(any());

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel);
        assertEquals("An unexpected error occurred. Please try again.", errorLabel.getText());
    }
}
