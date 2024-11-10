package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import api.GroupChatApiHandler;
import core.GroupChat;
import core.Message;
import core.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Test class for GroupChatController.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class GroupChatControllerTest {
    
    private GroupChatController controller;
    private User testUser;
    private String testGroupName;
    private Stage stage;
    private GroupChat groupChat;
    private List<Message> testMessages;
    private TestGroupChatApiHandler testApiHandler;

    static private boolean headless = true;

    // Test implementation of GroupChatApiHandler
    private class TestGroupChatApiHandler extends GroupChatApiHandler {
        private final GroupChat groupChat;
        private boolean shouldReturnEmpty = false;

        public TestGroupChatApiHandler(GroupChat groupChat) {
            this.groupChat = groupChat;
        }

        @Override
        public Optional<GroupChat> getGroupChat(String groupName) {
            return shouldReturnEmpty ? Optional.empty() : Optional.of(groupChat);
        }

        @Override
        public boolean sendMessage(String groupName, Message message) {
            groupChat.addMessage(message);
            return true;
        }

        public void setShouldReturnEmpty(boolean shouldReturnEmpty) {
            this.shouldReturnEmpty = shouldReturnEmpty;
        }
    }

    @BeforeAll
    static void setupHeadlessMode() {
        if(headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    private List<Message> createTestMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user1", "Message 1"));
        messages.add(new Message("user2", "Message 2"));
        messages.add(new Message("testUser", "Message 3"));
        return messages;
    }

    /**
     * Initializes the test data before the @Start method.
     */
    private void initializeTestData() {
        testUser = new User();
        testUser.setUsername("testUser");
        testGroupName = "testGroup";
        testMessages = createTestMessages();
        
        groupChat = new GroupChat();
        for (Message message : testMessages) {
            groupChat.addMessage(message);
        }

        testApiHandler = new TestGroupChatApiHandler(groupChat);
    }

    @Start
    private void start(Stage stage) {
        try {
            this.stage = stage;

            // Initialize test data
            initializeTestData();

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/GroupChatScreen.fxml"));
            Parent root = loader.load();

            // Get the controller instance created by FXMLLoader
            controller = loader.getController();

            // Inject test API handler using the setter
            controller.setGroupChatApiHandler(testApiHandler);

            // Set up scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Initialize controller
            Platform.runLater(() -> controller.initializeGroupChatWindow(testUser, testGroupName));

            // Wait for all events to process
            WaitForAsyncUtils.waitForFxEvents();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to start application: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test send message functionality")
    void testSendMessage(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();

        int initialCount = groupChat.getMessages().size();

        // Simulate typing a message
        robot.clickOn("#messageTextArea").write("Test message");
        robot.clickOn("#sendMessage");

        WaitForAsyncUtils.waitForFxEvents();

        // Assert that the message count has increased
        assertEquals(initialCount + 1, groupChat.getMessages().size(), "Message count should have increased by 1");

        // Get the last message
        Message lastMessage = groupChat.getMessages().get(groupChat.getMessages().size() - 1);

        // Verify the message content
        assertEquals("Test message", lastMessage.getText(), "Last message text should be 'Test message'");
        assertEquals(testUser.getUsername(), lastMessage.getAuthor(), "Last message author should be the test user");

        // Verify that the UI has updated
        VBox messageBox = robot.lookup("#vboxMessages").queryAs(VBox.class);
        assertNotNull(messageBox, "Message box should not be null");

        // Check that the message box has the correct number of messages
        assertEquals(initialCount + 1, messageBox.getChildren().size(), "Message box should contain the new message");

        // Verify the last message in the UI
        TextArea lastMessageArea = (TextArea) messageBox.getChildren().get(messageBox.getChildren().size() - 1);
        assertTrue(lastMessageArea.getText().contains("Test message"), "UI should display 'Test message'");
        assertTrue(lastMessageArea.getText().contains(testUser.getUsername()), "UI should display the test user's username");
    }

    // @Test
    // @DisplayName("Test message view initialization")
    // void testMessageViewInitialization(FxRobot robot) {
    //     WaitForAsyncUtils.waitForFxEvents();
        
    //     VBox messageBox = robot.lookup("#vboxMessages").queryAs(VBox.class);
    //     assertNotNull(messageBox);
    //     assertEquals(3, messageBox.getChildren().size());
        
    //     TextArea firstMessage = (TextArea) messageBox.getChildren().get(0);
    //     assertTrue(firstMessage.getText().contains("Message 1"));
    //     assertTrue(firstMessage.getText().contains("user1"));
    // }

    // @Test
    // @DisplayName("Test message load failure")
    // void testMessageLoadFailure(FxRobot robot) {
    //     testApiHandler.setShouldReturnEmpty(true);
        
    //     Platform.runLater(() -> controller.updateMessageView());
        
    //     WaitForAsyncUtils.waitForFxEvents();
        
    //     VBox messageBox = robot.lookup("#vboxMessages").queryAs(VBox.class);
    //     assertTrue(messageBox.getChildren().isEmpty());
    // }

    // @Test
    // @DisplayName("Test message formatting")
    // void testMessageFormatting(FxRobot robot) {
    //     WaitForAsyncUtils.waitForFxEvents();
        
    //     VBox messageBox = robot.lookup("#vboxMessages").queryAs(VBox.class);
    //     assertNotNull(messageBox);
    //     assertFalse(messageBox.getChildren().isEmpty());
        
    //     TextArea messageArea = (TextArea) messageBox.getChildren().get(0);
    //     assertNotNull(messageArea);
        
    //     assertFalse(messageArea.isEditable());
    //     assertTrue(messageArea.isWrapText());
    //     assertEquals(50, messageArea.getMinHeight());
    //     assertEquals(50, messageArea.getMaxHeight());
    // }
}
