package ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.GroupChat;
import core.Message;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.api.GroupChatApiHandler;

/**
 * Unit tests for the GroupChatController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class GroupChatControllerTest {

    private GroupChatController controller;

    @Mock
    private GroupChatApiHandler mockGroupChatApiHandler;

    @Mock
    private User mockUser;

    private String groupName = "TestGroup";

    /**
     * Initializes the test environment by loading the GroupChat.fxml and setting up dependencies.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    public void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        
        URL fxmlUrl = getClass().getResource("/ui/GroupChat.fxml");
        assertNotNull(fxmlUrl, "FXML file not found! Check the resource path.");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        controller = loader.getController();

        // Set up mocks
        when(mockUser.getUsername()).thenReturn("testUser");

        // Inject mocks into the controller using reflection
        setPrivateField(controller, "groupChatApiHandler", mockGroupChatApiHandler);
        setPrivateField(controller, "user", mockUser);
        setPrivateField(controller, "groupName", groupName);

        // Initialize the controller with mocked user and group name
        controller.initializeGroupChatWindow(mockUser, groupName);

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // Prepare a mock GroupChat with predefined messages
        Message msg1 = new Message("Alice", "Hello everyone!");
        Message msg2 = new Message("Bob", "Hi Alice!");
        GroupChat mockGroupChat = new GroupChat();
        mockGroupChat.addMessage(msg1);
        mockGroupChat.addMessage(msg2);

        // Define behavior for getGroupChat
        when(mockGroupChatApiHandler.getGroupChat(groupName)).thenReturn(Optional.of(mockGroupChat));

        // Define behavior for sendMessage
        doNothing().when(mockGroupChatApiHandler).sendMessage(eq(groupName), any(Message.class));
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
        Field field = GroupChatController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}