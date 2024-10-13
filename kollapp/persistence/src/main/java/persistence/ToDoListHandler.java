package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class ToDoListHandler {
    private final String toDoListPath;
    private final String groupToDoListPath;
    private ObjectMapper mapper;

    /**
     * Constructs a new ToDoListHandler.
     * Initializes the paths for individual and group to-do lists and sets up the ObjectMapper with the JavaTimeModule.
     * 
     * <p>The paths are set relative to the current directory:<p>
     * <ul>
     *   <li>toDoListPath: Path to the directory containing individual to-do lists.</li>
     *   <li>groupToDoListPath: Path to the directory containing group to-do lists.</li>
     * </ul>
     * 
     * The ObjectMapper is configured to handle Java 8 date and time API classes by registering the JavaTimeModule.
     */
    public ToDoListHandler() {
        this.toDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
        this.groupToDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "grouptodolists").toString() + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    
    /**
     * Constructs a new ToDoListHandler with the specified file paths for the to-do list and group to-do list.
     * The ObjectMapper is configured to handle Java 8 date and time API classes by registering the JavaTimeModule.
     *
     * @param todolistPath the file path for the to-do list
     * @param grouptodolistPath the file path for the group to-do list
     * 
     */
    public ToDoListHandler(String todolistPath, String grouptodolistPath) {
        this.toDoListPath = todolistPath;
        this.groupToDoListPath = grouptodolistPath;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Assigns a new ToDoList to the specified user by creating a new ToDoList object
     * and saving it to a JSON file named after the user's username.
     *
     * @param user the user to whom the ToDoList will be assigned
     */
    public void assignToDoList(User user) {
        ToDoList toDoList = new ToDoList();
        File file = new File(toDoListPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads the to-do list for the specified user from a JSON file.
     *
     * @param user the user whose to-do list is to be loaded
     * @return the loaded ToDoList object
     * @throws IllegalArgumentException if the to-do list file does not exist or if there is an error during loading
     */
    public ToDoList loadToDoList(User user) {
        File file = new File(toDoListPath + user.getUsername() + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("To-do list file does not exist for user: " + user.getUsername());
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for user: " + user.getUsername());
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Updates the to-do list for a given user by writing the to-do list to a JSON file.
     *
     * @param user The user whose to-do list is being updated.
     * @param toDoList The to-do list to be written to the file.
     */
    public void updateToDoList(User user, ToDoList toDoList) {
        File file = new File(toDoListPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the to-do list for a given user group from a JSON file.
     *
     * @param userGroup the user group whose to-do list is to be loaded
     * @return the loaded ToDoList object; if the file does not exist or an error occurs,
     *         an empty ToDoList object is returned
     */
    public ToDoList loadGroupToDoList(UserGroup userGroup) {
        String groupName = userGroup.getGroupName();
        File file = new File(groupToDoListPath + groupName + ".json");
        if (!file.exists()) {
            System.out.println("File not found for user: " + groupName);
            return new ToDoList(); // Return an empty list instead of null if file doesn't exist
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for group: " + groupName);
            e.printStackTrace();
        }
        return new ToDoList(); // Return an empty list instead of null if an exception occurs
    }

    /**
     * Updates the to-do list for a specified user group.
     *
     * @param userGroup the user group whose to-do list is to be updated
     * @param toDoList the new to-do list to be saved for the user group
     * @throws IllegalArgumentException if the to-do list file does not exist for the specified group
     */
    public void updateGroupToDoList(UserGroup userGroup, ToDoList toDoList) {
        File file = new File(groupToDoListPath + userGroup.getGroupName() + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("To-do list file does not exist for group: " + userGroup.getGroupName());
        }
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
