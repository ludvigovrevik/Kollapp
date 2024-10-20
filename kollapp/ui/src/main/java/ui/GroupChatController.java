package ui;

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
    private String author;
    private GroupChatHandler groupChatHandler;
    private String groupName;  // The name of the group for which we are loading messages

    // Initialize the GroupChat window and bind it with the group chat handler
    @FXML
    public void initializeGroupChatWindow(User user, String groupName) {
        this.user = user;
        this.groupName = groupName; // Set the group name (e.g., "GroupChat1")
        this.author = user.getUsername();
        this.groupChatHandler = new GroupChatHandler(); // Initialize the handler
        this.groupChatHandler.createGroupChat(author, groupName, "");
        
        // Display the current messages in the view
        updateMessageView();
    }

    // Handle sending messages
    @FXML
    private void handleSendMessage() {
        String text = messageTextArea.getText(); // Get the text from the input box
        // if (this.groupChatHandler.getGroupChat(groupName) == null) {
        //     this.groupChatHandler.createGroupChat(author, groupName, text);
        // }

        this.groupChatHandler.createGroupChat(author, groupName, text);

        // Replace "username" with the actual user's name
        Message message = new Message("username", text);

        // Append the message to the JSON file using GroupChatHandler
        groupChatHandler.appendMessage(user,this.groupName, text);

        // Clear the message input box
        messageTextArea.clear();

        // Update the chat view to show the new message
        updateMessageView();
    }

    // Update the chat window with messages from the JSON file
    private void updateMessageView() {
        Message groupChat = groupChatHandler.getGroupChat(this.groupName); // Get all messages for the group
        List<List<String>> messages = groupChat.getMessages();

        // Clear the VBox before adding updated messages
        vboxMessages.getChildren().clear();

        for (int i = 0; i < messages.size(); i++) {
            TextArea messageArea = new TextArea(messages.get(i).get(0) + ": " + messages.get(i).get(1));
            messageArea.setWrapText(true); // Wrap text for longer messages
            messageArea.setEditable(false); // The message box should be read-only
            vboxMessages.getChildren().add(messageArea); // Add the message to the VBox
        }
         // Scroll to the bottom to show the latest message
         viewMessagePane.setVvalue(1.0);
    }
}

