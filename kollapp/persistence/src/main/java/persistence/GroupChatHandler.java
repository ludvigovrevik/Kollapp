package persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.Message;
import javafx.scene.Group;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GroupChatHandler {
    private final ObjectMapper mapper;
    private final String groupChatPath;
    private Message message;

    public GroupChatHandler() {
        this.groupChatPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groupchat").toString() + File.separator;
        this.mapper = new ObjectMapper();
    }

    public void createGroupChat(String groupName) {
        File fileForGroupChat = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(fileForGroupChat, new ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create group chat");
        }
    }

    public void appendMessage(String groupName, Message message) {
        File fileForGroupChat = new File(groupChatPath + groupName + ".json");
        List<Message> messages = new ArrayList<>();
        try {
            messages = mapper.readValue(fileForGroupChat, new TypeReference<List<Message>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read messages");
        }

        messages.add(message);
        try {
            mapper.writeValue(fileForGroupChat, messages);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save messages");
        }
    }

    public List<Message> getMessages(String groupName) {
        File fileForGroupChat = new File(groupChatPath + groupName + ".json");
        List<Message> messages = new ArrayList<>();
        try {
            messages = mapper.readValue(fileForGroupChat, new TypeReference<List<Message>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read messages");
        }
        return messages;
    }

}