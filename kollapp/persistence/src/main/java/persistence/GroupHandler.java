package persistence;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class GroupHandler {
    private final String GROUP_PATH;
    private final String GROUP_TODOLIST_PATH;
    private ObjectMapper mapper = new ObjectMapper();
    private UserHandler userHandler;

    public GroupHandler() {
        this.GROUP_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toString() + File.separator;
        this.GROUP_TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toString() + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
        userHandler = new UserHandler();
    }

    public GroupHandler(String groupPath, String groupToDoListPath, UserHandler userHandler) {
        this.GROUP_PATH = groupPath;
        this.GROUP_TODOLIST_PATH = groupToDoListPath;
        this.mapper.registerModule(new JavaTimeModule());
        this.userHandler = userHandler;
    }

    public void createGroup(User user, String groupName) {
        UserGroup userGroup = new UserGroup(groupName);
        userGroup.addUser(user.getUsername());
        user.addUserGroup(groupName);

        ToDoList toDoList = new ToDoList();

        File fileForGroup = new File(GROUP_PATH + groupName + ".json");
        File fileGroupToDoList = new File(GROUP_TODOLIST_PATH + groupName + ".json");

        try {
            mapper.writeValue(fileForGroup, userGroup);
            mapper.writeValue(fileGroupToDoList, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create group");
        }
        userHandler.updateUser(user);
    }

    public UserGroup getGroup(String groupName) {
        File file = new File(GROUP_PATH + groupName + ".json");
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

    public void assignUserToGroup(User user, String groupName) {
        // Retrieve the UserGroup object
        UserGroup group = getGroup(groupName);

        // Add user to group
        group.addUser(user.getUsername());

        // Add the group to the user's list of groups
        user.addUserGroup(groupName);

        // Update the user information
        userHandler.updateUser(user);

        // **Save the updated group back to the file**
        File fileForGroup = new File(GROUP_PATH + groupName + ".json");
        try {
            mapper.writeValue(fileForGroup, group);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update group file");
        }
    }
}
