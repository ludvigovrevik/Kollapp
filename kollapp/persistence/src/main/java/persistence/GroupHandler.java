package persistence;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class GroupHandler {
    private static final String TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toString() + File.separator;
    private static ObjectMapper mapper = new ObjectMapper();


    public static void createGroup(User user, String groupName) {
        UserGroup userGroup = new UserGroup(groupName);
        userGroup.addUser(user.getUsername());
        userGroup.setToDoList(new ToDoList());

        user.addUserGroup(groupName);
        
        File file = new File(TODOLIST_PATH + groupName + ".json");
        try {
            mapper.writeValue(file, userGroup);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create group");
        }
        UserHandler.updateUser(user);
    }

    public static UserGroup getGroup(String groupName) {
        File file = new File(TODOLIST_PATH + groupName + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("Group file does not exist: " + file.getPath());
        }
        
        try {
            System.out.println("Reading file: " + file.getPath());
            UserGroup userGroup = mapper.readValue(file, UserGroup.class);
            return userGroup; 
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error reading the group file: " + e.getMessage());
        }
    }

    public static void assignUserToGroup(User user, String groupName) {
        // Retrieve the UserGroup object
        UserGroup group = GroupHandler.getGroup(groupName);
        
        // Add user to group
        group.addUser(user.getUsername());

        // Add the group to the user's list of groups
        user.addUserGroup(groupName);

        // Update the user information
        UserHandler.updateUser(user);
    }
}

