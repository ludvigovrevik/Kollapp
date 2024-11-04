package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.Message;
import core.GroupChat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;

@Component
public class GroupChatHandler {
    private final ObjectMapper mapper;
    private final String groupChatPath;

    public GroupChatHandler() {
        // Define the path where group chat files are stored
        this.groupChatPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groupchat") + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public GroupChatHandler(String path) {
        this.groupChatPath = path;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void createGroupChat(String groupName) {
        GroupChat groupChat = new GroupChat();
        File groupChatFile = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(groupChatFile, groupChat);
        } catch (IOException e) {
            System.out.println(groupChat + groupChatFile.getAbsolutePath());
            throw new RuntimeException("Failed to create group chat");
        }
    }

    public GroupChat getGroupChat(String groupName) {
        File groupChatFile = new File(groupChatPath + groupName + ".json");
        if (!groupChatFile.exists()) {
            return new GroupChat();
        }

        try {
            return mapper.readValue(groupChatFile, GroupChat.class);   
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load group chat");
        }
    }
    
    // Append a message to the group chat file
    public void sendMessage(String groupName, Message message) {
        GroupChat groupChat = getGroupChat(groupName);
        groupChat.addMessage(message);

        File groupChatFile = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(groupChatFile, groupChat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update group chat file");
        }
    }
}