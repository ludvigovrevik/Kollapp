package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import api.GroupApiHandler;
import api.GroupChatApiHandler;
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

@ExtendWith(ApplicationExtension.class)
class RegisterGroupControllerTest {
    
    private RegisterGroupController controller;
    private GroupApiHandler mockGroupApiHandler;
    private UserApiHandler mockUserApiHandler;
    private GroupChatApiHandler mockGroupChatHandler;
    private KollAppController mockKollAppController;
    private User mockUser;
    private Stage stage;
    private TextField groupNameField;
    private Label errorLabel;
    private static final boolean headless = true;

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
    private void start(Stage stage) throws Exception {
        this.stage = stage;
        
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
    void setUp() {
        // Create fresh mocks
        mockGroupApiHandler = mock(GroupApiHandler.class);
        mockUserApiHandler = mock(UserApiHandler.class);
        mockGroupChatHandler = mock(GroupChatApiHandler.class);
        mockKollAppController = mock(KollAppController.class);
        mockUser = mock(User.class);

        // Setup default mock behavior
        when(mockUser.getUsername()).thenReturn("testUser");
        when(mockGroupApiHandler.groupExists(anyString())).thenReturn(false);
        when(mockGroupApiHandler.createGroup(anyString(), anyString())).thenReturn(true);
        when(mockUserApiHandler.assignGroupToUser(anyString(), anyString())).thenReturn(true);

        // Initialize controller with mocks
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
    void testCreateGroup_Success() {
        // Arrange
        String groupName = "NewGroup";
        List<String> mockGroups = List.of("Group1", "Group2", groupName);
        when(mockUser.getUserGroups()).thenReturn(mockGroups);

        // Act
        setGroupNameAndCreateGroup(groupName);

        // Assert
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
        verify(mockUserApiHandler).assignGroupToUser("testUser", groupName);
        verify(mockKollAppController).populateGroupView(mockGroups);
    }

    @Test
    void testCreateGroup_EmptyGroupName() {
        // Act
        setGroupNameAndCreateGroup("");

        // Assert
        assertEquals("Group Name cannot be empty", errorLabel.getText());
        verify(mockGroupApiHandler, never()).createGroup(anyString(), anyString());
    }

    @Test
    void testCreateGroup_AlreadyExists() {
        // Arrange
        String groupName = "ExistingGroup";
        when(mockGroupApiHandler.groupExists(groupName)).thenReturn(true);

        // Act
        setGroupNameAndCreateGroup(groupName);

        // Assert
        assertEquals("Group already exists", errorLabel.getText());
        verify(mockGroupApiHandler, never()).createGroup(anyString(), anyString());
    }

    @Test
    void testCreateGroup_ExceptionDuringCreation() {
        // Arrange
        String groupName = "ExceptionGroup";
        when(mockGroupApiHandler.createGroup("testUser", groupName))
            .thenThrow(new RuntimeException("Creation failed"));

        // Act
        setGroupNameAndCreateGroup(groupName);

        // Assert
        assertEquals("An unexpected error occurred. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }

    @Test
    void testCreateGroup_CreationFails() {
        // Arrange
        String groupName = "FailedGroup";
        when(mockGroupApiHandler.createGroup("testUser", groupName)).thenReturn(false);

        // Act
        setGroupNameAndCreateGroup(groupName);

        // Assert
        assertEquals("Failed to create group. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }

    @Test
    void testCreateGroup_UserAssignmentFails() {
        // Arrange
        String groupName = "UnassignedGroup";
        when(mockUserApiHandler.assignGroupToUser("testUser", groupName)).thenReturn(false);

        // Act
        setGroupNameAndCreateGroup(groupName);

        // Assert
        assertEquals("Failed to assign user to group. Please try again.", errorLabel.getText());
        verify(mockGroupApiHandler).createGroup("testUser", groupName);
    }
}