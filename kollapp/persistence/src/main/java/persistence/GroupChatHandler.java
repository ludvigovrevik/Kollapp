package persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.Message;
import core.User;
import core.UserGroup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GroupChatHandler {
    private final ObjectMapper mapper;
    private final String groupChatPath;

    public GroupChatHandler() {
        // Define the path where group chat files are stored
        this.groupChatPath = Paths.get("groupchat").toString() + File.separator;
        this.mapper = new ObjectMapper();
    }

        // Create a new group chat file (if it doesn't exist)
    public void createGroupChat(String author, String groupName, String text) {
        Message localMessage = new Message(author, text);
        
        File fileForGroupChat = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(fileForGroupChat, localMessage);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // Create a new group chat file (if it doesn't exist)
    public void createGroupChat(User user, String groupName, String text) {
        String author = user.getUsername();
        Message localMessage = new Message(author, "");
        
        File fileForGroupChat = new File(groupChatPath + groupName + ".json");
        try {
            mapper.writeValue(fileForGroupChat, localMessage);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // public Message getGroupChat(String groupName) {
    //     File fileForGroupChat = new File(groupChatPath + groupName + ".json");
    //     if (!fileForGroupChat.exists()) {
            
    //         String author = user.getUsername();
    //         Message localMessage = new Message(author, "");

    //         File fileForGroupChat = new File(groupChatPath + groupName + ".json");
    //         try {
    //             mapper.writeValue(fileForGroupChat, localMessage);
    //         } catch (Exception e) {
    //             // TODO: handle exception
    //         }


    //     }
    //     try {
    //         return mapper.readValue(fileForGroupChat, Message.class);
    //     } catch (IOException e) {
    //         throw new IllegalArgumentException("Error reading the group chat file: " + e.getMessage());
    //     }
    // }

    // Append a message to the group chat file
    // public void appendMessage(User user, String groupName, String text ) {
    //     Message groupChat = getGroupChat(groupName);
    //     groupChat.addMessage(text);

    //     File fileForGroupChat = new File(groupChatPath + groupName + ".json");
    //     try {
    //         mapper.writeValue(fileForGroupChat, groupChat);
    //     } catch (Exception e) {
    //         throw new RuntimeException("Failed to update group chat file");
    //     }
    // }
}