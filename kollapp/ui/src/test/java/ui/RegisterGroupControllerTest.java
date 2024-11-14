package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import api.GroupApiHandler;
import api.UserApiHandler;
import core.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link RegisterGroupController} class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
class RegisterGroupControllerTest {

    private RegisterGroupController controller;
    private GroupApiHandler mockGroupApiHandler;
    private UserApiHandler mockUserApiHandler;
    private KollAppController mockKollAppController;
    private User mockUser;
    private TextField groupNameField;
    private Label errorLabel;
    private static final boolean headless = true;

    /**
     * Sets up headless mode for TestFX tests if enabled.
     * This allows tests to run in environments without a GUI.
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
     * Initializes the test environment by loading the RegisterGroup.fxml
     * and setting up the controller.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    private void start(Stage stage) throws Exception {
        URL fxmlUrl = getClass().getResource("/ui/RegisterGroup.fxml");
        assertNotNull(fxmlUrl, "FXML file not found!");

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        controller = loader.getController();
        assertNotNull(controller, "Controller not initialized!");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        groupNameField = (TextField) scene.lookup("#groupNameField");
        errorLabel = (Label) scene.lookup("#errorLabel");

        assertNotNull(groupNameField, "Group name field not found!");
        assertNotNull(errorLabel, "Error label not found!");

        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    private void setUp() {
        mockGroupApiHandler = mock(GroupApiHandler.class);
        mockUserApiHandler = mock(UserApiHandler.class);
        mockKollAppController = mock(KollAppController.class);
        mockUser = mock(User.class);

        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockGroupApiHandler.groupExists(anyString())).thenReturn(false);
        when(mockGroupApiHandler.createGroup(anyString(), anyString())).thenReturn(true);
        when(mockUserApiHandler.assignGroupToUser(anyString(), anyString())).thenReturn(true);

        Platform.runLater(() -> {
            controller.initialize(mockUser, mockKollAppController);
            controller.setGroupApiHandler(mockGroupApiHandler);
            controller.setUserApiHandler(mockUserApiHandler);
            groupNameField.setText("");
            errorLabel.setText("");
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    private void setGroupNameAndCreateGroup(String groupName) {
        Platform.runLater(() -> {
            groupNameField.setText(groupName);
            controller.createGroup(new ActionEvent());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("Test successful group creation")
    @Tag("group")
    public void testCreateGroup_Success() {
        String groupName = "NewGroup";
        List<String> mockGroups = new ArrayList<>(List.of("Group1", "Group2"));
        when(mockUser.getUserGroups()).thenReturn(mockGroups);

        setGroupNameAndCreateGroup(groupName);
        mockGroups.add(groupName);

        verify(mockGroupApiHandler).createGroup("testUser", groupName);
        verify(mockUserApiHandler).assignGroupToUser("testUser", groupName);
        verify(mockKollAppController).populateGroupView(mockGroups);
    }

    @Test
    @DisplayName("Test empty group name validation")
    @Tag("group")
    public void testCreateGroup_EmptyGroupName() {
        setGroupNameAndCreateGroup("");

        assertEquals("Group Name cannot be empty", errorLabel.getText());
        verify(mockGroupApiHandler, never()).createGroup(anyString(), anyString());
    }

    @Test
    @DisplayName("Test group already exists validation")
    @Tag("group")
    public void testCreateGroup_AlreadyExists() {
        String groupName = "ExistingGroup";
        when(mockGroupApiHandler.groupExists(groupName)).thenReturn(true);

        setGroupNameAndCreateGroup(groupName);

        assertEquals("Group already exists", errorLabel.getText());
        verify(mockGroupApiHandler, never()).createGroup(anyString(), anyString());
    }

    @Test
    @DisplayName("Test handling of exception during group creation")
    @Tag("group")
    public void testCreateGroup_ExceptionDuringCreation() {
        String groupName = "ExceptionGroup";
        when(mockGroupApiHandler.createGroup("testUser", groupName)).thenThrow(new RuntimeException("Creation failed"));

        setGroupNameAndCreateGroup(groupName);

        assertEquals("An unexpected error occurred. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }

    @Test
    @DisplayName("Test handling of failed group creation")
    @Tag("group")
    public void testCreateGroup_CreationFails() {
        String groupName = "FailedGroup";
        when(mockGroupApiHandler.createGroup("testUser", groupName)).thenReturn(false);

        setGroupNameAndCreateGroup(groupName);

        assertEquals("Failed to create group. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }

    @Test
    @DisplayName("Test handling of failed user assignment to group")
    @Tag("group")
    public void testCreateGroup_UserAssignmentFails() {
        String groupName = "UnassignedGroup";
        when(mockUserApiHandler.assignGroupToUser("testUser", groupName)).thenReturn(false);

        setGroupNameAndCreateGroup(groupName);

        assertEquals("Failed to assign user to group. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }
}
