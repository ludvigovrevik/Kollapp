package ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import core.GroupChat;
import core.Message;
import core.User;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import ui.api.GroupChatApiHandler;

/**
 * Controller class for handling the group chat UI.
 */
public class GroupChatController {

    @FXML
    private ScrollPane viewMessagePane;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private VBox vboxMessages;

    private User user;
    private GroupChatApiHandler groupChatApiHandler;
    private String groupName; 

    /**
     * Initializes the GroupChat window and binds it with the group chat handler.
     *
     * @param user      the user participating in the chat
     * @param groupName the name of the group chat
     */
    @FXML
    public void initializeGroupChatWindow(User user, String groupName) {
        this.user = user;
        this.groupName = groupName;
        this.groupChatApiHandler = new GroupChatApiHandler(); // Initialize the handler
        updateMessageView();
    }

    /**
     * Handles sending messages.
     */
    @FXML
    private void handleSendMessage() {
        String text = messageTextArea.getText();
        Message message = new Message(this.user.getUsername(), text);
        groupChatApiHandler.sendMessage(this.groupName, message);

        updateMessageView();
    }
    
    /**
     * Updates the message view with the latest messages.
     */
    private void updateMessageView() {
        GroupChat groupChat = groupChatApiHandler.getGroupChat(this.groupName).get();
        List<Message> messages = groupChat.getMessages();

        // Clear the VBox before adding updated messages
        vboxMessages.getChildren().clear();

        // Add each message to the VBox
        for (Message message : messages) {
            String author = message.getAuthor();
            String text = message.getText();
            LocalDateTime timestamp = message.getTimestamp();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            String formattedTimestamp = timestamp.format(formatter);
            TextArea messageArea = new TextArea("[" + formattedTimestamp + "] " + author + ": " + text);

            // Set the message area properties
            messageArea.setWrapText(true);
            messageArea.setEditable(false); 
            messageArea.setMinHeight(50); 
            messageArea.setMaxHeight(50);

            // Add the message to the VBox
            vboxMessages.getChildren().add(messageArea);

            // Clear the message text area after sending the message
            messageTextArea.clear();
        }
         // Scroll to the bottom to show the latest message
        viewMessagePane.setVvalue(1.0);
    }
}

