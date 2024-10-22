package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.MessageLog;
import core.ToDoList;
import core.User;
import core.UserGroup;

public class GroupHandler {
    private final String groupPath;
    private final String groupToDoListPath;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserHandler userHandler;
    // private final GroupChatHandler groupChatHandler;

    /**
     * Constructs a new GroupHandler instance.
     * Initializes the paths for group and group to-do list directories.
     * 
     * <p>The paths are set relative to the current directory:<p>
     * <ul>
     *   <li>groupPath: Path to the directory containing groups.</li>
     *   <li>groupToDoListPath: Path to the directory containing groups to-do lists.</li>
     * </ul>
     * 
     * Registers the JavaTimeModule with the ObjectMapper and initializes the UserHandler instance.
     */
    public GroupHandler() {
        this.groupPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups") + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists") + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
        this.userHandler = new UserHandler();
        // this.groupChatHandler = new GroupChatHandler();
    }

    /**
     * Constructs a new GroupHandler with the specified paths and user handler.
     *
     * @param groupPath the path to the group data
     * @param groupToDoListPath the path to the group to-do list data
     * @param userHandler the handler for user-related operations
     */
    public GroupHandler(String groupPath, String groupToDoListPath, UserHandler userHandler) {
        this.groupPath = groupPath;
        this.groupToDoListPath = groupToDoListPath;
        this.mapper.registerModule(new JavaTimeModule());
        this.userHandler = userHandler;
        // this.groupChatHandler = new GroupChatHandler();
    }

    /**
     * Creates a new user group with the specified group name and adds the given user to the group.
     * Also initializes a to-do list for the group and saves both the group and the to-do list to JSON files.
     *
     * @param user the user who is creating the group and will be added to the group
     * @param groupName the name of the group to be created
     * @throws RuntimeException if there is an error while creating the group or writing to the files
     */
    public void createGroup(User user, String groupName) {
        UserGroup userGroup = new UserGroup(groupName);
        userGroup.addUser(user.getUsername());
        user.addUserGroup(groupName);
        MessageLog messageLog = new MessageLog();
        ToDoList toDoList = new ToDoList();

        File fileForGroup = new File(groupPath + groupName + ".json");
        File fileGroupToDoList = new File(groupToDoListPath + groupName + ".json");
        
        try {
            mapper.writeValue(fileForGroup, userGroup);
            mapper.writeValue(fileGroupToDoList, toDoList);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create group");
        }
        userHandler.updateUser(user);
    }

    /**
     * Retrieves a UserGroup object from a JSON file based on the provided group name.
     *
     * @param groupName the name of the group to retrieve
     * @return the UserGroup object corresponding to the provided group name
     * @throws IllegalArgumentException if the group file does not exist or if there is an error reading the file
     */
    public UserGroup getGroup(String groupName) {
        File file = new File(groupPath + groupName + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("Group file does not exist: " + file.getPath());
        }
        try {
            return mapper.readValue(file, UserGroup.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading the group file: " + e.getMessage());
        }
    }

   
    /**
     * Assigns a user to a specified group.
     * Updates the group file with the new user added to the group.
     * Also Updates the user file with the new group added to the user.
     *
     * @param user the user to be assigned to the group
     * @param groupName the name of the group to which the user will be assigned
     * @throws RuntimeException if there is an error updating the group file
     */
    public void assignUserToGroup(User user, String groupName) {
        UserGroup group = getGroup(groupName);
        group.addUser(user.getUsername());
        user.addUserGroup(groupName);
        userHandler.updateUser(user);
        
        File fileForGroup = new File(groupPath + groupName + ".json");
        try {
            mapper.writeValue(fileForGroup, group);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update group file");
        }
    }

    /**
     * Checks if a group with the specified name exists.
     *
     * @param groupName the name of the group to check for existence
     * @return true if the group exists, false otherwise
     */
    public boolean groupExists(String groupName) {
        File file = new File(groupPath + groupName + ".json");
        return file.exists();
    }
}
