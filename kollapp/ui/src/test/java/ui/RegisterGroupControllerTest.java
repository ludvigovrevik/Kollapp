package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URL;

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
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.UserHandler;

/**
 * Unit tests for the RegisterGroupController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class RegisterGroupControllerTest {

    private RegisterGroupController controller;
    private GroupHandler mockGroupHandler;
    private UserHandler mockUserHandler;
    private KollAppController mockKollAppController;
    private User mockUser;

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

        mockGroupHandler = mock(GroupHandler.class);
        mockKollAppController = mock(KollAppController.class);
        mockUser = mock(User.class);

        setPrivateField(controller, "groupHandler", mockGroupHandler);
        setPrivateField(controller, "kollAppController", mockKollAppController);
        setPrivateField(controller, "user", mockUser);

        when(mockUser.getUsername()).thenReturn("testUser");

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp(FxRobot robot) {
        // Initialization before each test is handled in the start method
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

        doNothing().when(mockGroupHandler).createGroup(mockUser, groupName);

        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group");

        verify(mockGroupHandler, times(1)).createGroup(mockUser, groupName);
        verify(mockKollAppController, times(1)).populateGroupView();
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

        verify(mockGroupHandler, never()).createGroup(any(User.class), anyString());

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

        doThrow(new RuntimeException("Creation failed")).when(mockGroupHandler).createGroup(mockUser, groupName);

        robot.clickOn("#groupNameField").write(groupName);
        robot.clickOn("Create group");

        verify(mockGroupHandler, times(1)).createGroup(mockUser, groupName);
        verify(mockKollAppController, never()).populateGroupView();

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Failed to create group. Please try again.", errorLabel.getText());
    }
}
