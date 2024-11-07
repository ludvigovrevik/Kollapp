package api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.GroupChatHandler;
import core.GroupChat;
import core.Message;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service class for handling group chat operations.
 */
@Service
public class GroupChatService {

    private final GroupChatHandler groupChatHandler;
    private final String groupChatPath = Paths.get("..", "persistence", "src", "main", "java",
                "persistence", "groupchat").toAbsolutePath()
                .normalize().toString() + File.separator;
    /**
     * Constructor for GroupChatService.
     *
     * @param groupChatHandler the handler for group chat persistence
     */
    @Autowired
    public GroupChatService() {
        this.groupChatHandler = new GroupChatHandler(this.groupChatPath);
    }

    /**
     * Creates a new group chat with the specified name.
     *
     * @param groupName the name of the group chat to create
     * @throws IllegalArgumentException if the group chat already exists
     */
    public void createGroupChat(String groupName) {
        if (!groupChatHandler.getGroupChat(groupName).getMessages().isEmpty()) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' already exists.");
        }
        groupChatHandler.createGroupChat(groupName);
    }

    /**
     * Retrieves the group chat with the specified name.
     *
     * @param groupName the name of the group chat to retrieve
     * @return the GroupChat object
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public GroupChat getGroupChat(String groupName) {
        GroupChat groupChat = groupChatHandler.getGroupChat(groupName);
        if (groupChat == null) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' does not exist.");
        }
        return groupChat;
    }

    /**
     * Sends a message to the specified group chat.
     *
     * @param groupName the name of the group chat
     * @param message   the message to send
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public void sendMessage(String groupName, Message message) {
        GroupChat groupChat = groupChatHandler.getGroupChat(groupName);
        if (groupChat == null) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' does not exist.");
        }
        groupChatHandler.sendMessage(groupName, message);
    }

    /**
     * Retrieves all messages from the specified group chat.
     *
     * @param groupName the name of the group chat
     * @return a list of messages
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public List<Message> getMessages(String groupName) {
        GroupChat groupChat = groupChatHandler.getGroupChat(groupName);
        if (groupChat == null) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' does not exist.");
        }
        return groupChat.getMessages();
    }
}