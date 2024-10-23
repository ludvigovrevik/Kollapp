package ui;

import core.GroupChat;
import core.Message;
import core.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import persistence.GroupChatHandler;

import java.util.List;

public class GroupChatController {

    @FXML
    private ScrollPane viewMessagePane;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private Button sendMessage;

    @FXML
    private VBox vboxMessages; // VBox that holds the messages

    private User user;
    private GroupChatHandler groupChatHandler;
    private String groupName;  // The name of the group for which we are loading messages

    // Initialize the GroupChat window and bind it with the group chat handler
    @FXML
    public void initializeGroupChatWindow(User user, String groupName) {
        this.user = user;
        this.groupName = groupName; // Set the group name (e.g., "GroupChat1")
        this.groupChatHandler = new GroupChatHandler(); // Initialize the handler
        updateMessageView();
    }

    // Handle sending messages
    @FXML
    private void handleSendMessage() {
        String text = messageTextArea.getText(); // Get the text from the input box
        Message message = new Message(this.user.getUsername(), text);
        groupChatHandler.sendMessage(this.groupName, message);
        applyChatBoxStyle(text);
        updateMessageView();
    }

    private void applyChatBoxStyle(String text) {
         // Create a new TextArea for the message
        TextArea messageArea = new TextArea(text);
        messageArea.setWrapText(true); // Wrap text for longer messages
        messageArea.setEditable(false); // The message box should be read-only
    
        // Disable resizing of TextArea to preserve uniformity
        messageArea.setMinHeight(50); 
        messageArea.setMaxHeight(50);
    
        // Add the message to the VBox
        vboxMessages.getChildren().add(messageArea);
    
        // Clear the message input box
        messageTextArea.clear();
    
        // Scroll to the bottom to show the latest message
        viewMessagePane.layout(); // Ensure the layout is updated before scrolling
        viewMessagePane.setVvalue(1.0); // Scroll to the bottom
    }
    

    // Update the chat window with messages from the JSON file
    private void updateMessageView() {
        GroupChat groupChat = groupChatHandler.getGroupChat(this.groupName); // Get all messages for the group
        List<Message> messages = groupChat.getMessages();

        // Clear the VBox before adding updated messages
        vboxMessages.getChildren().clear();

        for (int i = 0; i < messages.size(); i++) {
            String author = messages.get(i).getAuthor();
            String text = messages.get(i).getText();
            TextArea messageArea = new TextArea(author + ": " + text);

            messageArea.setWrapText(true); // Wrap text for longer messages
            messageArea.setEditable(false); // The message box should be read-only
            vboxMessages.getChildren().add(messageArea); // Add the message to the VBox
        }
         // Scroll to the bottom to show the latest message
         viewMessagePane.setVvalue(1.0);
    }
}

