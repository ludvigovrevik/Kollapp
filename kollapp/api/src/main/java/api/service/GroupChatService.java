package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.GroupChat;
import core.Message;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service class for handling group chat operations without a handler.
 */
@Service
public class GroupChatService {

    private final ObjectMapper mapper;
    private final String groupChatPath;

    public GroupChatService() {
        this.groupChatPath = Paths.get("..", "persistence", "src", "main", "java",
                "persistence", "groupchat").toAbsolutePath().normalize().toString() + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Creates a new group chat with the specified name.
     *
     * @param groupName the name of the group chat to create
     * @throws IllegalArgumentException if the group chat already exists
     */
    public void createGroupChat(String groupName) {
        File groupChatFile = new File(groupChatPath + groupName + ".json");
        if (groupChatFile.exists()) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' already exists.");
        }

        GroupChat groupChat = new GroupChat();
        try {
            mapper.writeValue(groupChatFile, groupChat);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create group chat", e);
        }
    }

    /**
     * Retrieves the group chat with the specified name.
     *
     * @param groupName the name of the group chat to retrieve
     * @return the GroupChat object
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public GroupChat getGroupChat(String groupName) {
        File groupChatFile = new File(groupChatPath + groupName + ".json");
        if (!groupChatFile.exists()) {
            throw new IllegalArgumentException("Group chat with name '" + groupName + "' does not exist.");
        }

        try {
            return mapper.readValue(groupChatFile, GroupChat.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load group chat", e);
        }
    }

    /**
     * Sends a message to the specified group chat.
     *
     * @param groupName the name of the group chat
     * @param message   the message to send
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public void sendMessage(String groupName, Message message) {
        GroupChat groupChat = getGroupChat(groupName);
        groupChat.addMessage(message);

        File groupChatFile = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(groupChatFile, groupChat);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update group chat file", e);
        }
    }

    /**
     * Retrieves all messages from the specified group chat.
     *
     * @param groupName the name of the group chat
     * @return a list of messages
     * @throws IllegalArgumentException if the group chat does not exist
     */
    public List<Message> getMessages(String groupName) {
        return getGroupChat(groupName).getMessages();
    }
}