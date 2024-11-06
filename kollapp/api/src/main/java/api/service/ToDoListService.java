package api.service;

import core.ToDoList;
import core.User;
import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.ToDoListHandler;
import persistence.UserHandler;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class ToDoListService {

    private final ToDoListHandler toDoListHandler;
    private final UserHandler userHandler;

    @Autowired
    public ToDoListService() {
        String baseDir = System.getProperty("user.dir");
        String toDoListPath = Paths.get(baseDir, "persistence", "todolists").toString();
        String groupToDoListPath = Paths.get(baseDir, "persistence", "grouptodolists").toString();
        String userPath = Paths.get(baseDir, "persistence", "users").toString();
        
        // Ensure directories exist
        new File(toDoListPath).mkdirs();
        new File(groupToDoListPath).mkdirs();
        new File(userPath).mkdirs();

        this.userHandler = new UserHandler(userPath);
        this.toDoListHandler = new ToDoListHandler(toDoListPath, groupToDoListPath);
    }

    /**
     * Assigns a new to-do list to the specified user.
     *
     * @param username the username of the user
     */
    public void assignToDoList(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            try {
                toDoListHandler.assignToDoList(userOpt.get());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to assign to-do list to user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Loads the to-do list for the specified user.
     *
     * @param username the username of the user
     * @return the to-do list of the user
     */
    public ToDoList loadToDoList(String username) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            try {
                return toDoListHandler.loadToDoList(userOpt.get());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to load to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Updates the to-do list for the specified user.
     *
     * @param username the username of the user
     * @param toDoList the new to-do list
     */
    public void updateToDoList(String username, ToDoList toDoList) {
        Optional<User> userOpt = userHandler.getUser(username);
        if (userOpt.isPresent()) {
            try {
                toDoListHandler.updateToDoList(userOpt.get(), toDoList);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to update to-do list for user: " + username, e);
            }
        } else {
            throw new IllegalArgumentException("User not found: " + username);
        }
    }

    /**
     * Loads the to-do list for the specified user group.
     *
     * @param groupName the name of the user group
     * @return the to-do list of the group
     */
    public ToDoList loadGroupToDoList(String groupName) {
        UserGroup userGroup = new UserGroup(groupName);
        try {
            return toDoListHandler.loadGroupToDoList(userGroup);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to load group to-do list for group: " + groupName, e);
        }
    }

    /**
     * Updates the to-do list for the specified user group.
     *
     * @param groupName the name of the user group
     * @param toDoList  the new to-do list
     */
    public void updateGroupToDoList(String groupName, ToDoList toDoList) {
        UserGroup userGroup = new UserGroup(groupName);
        try {
            try {
                toDoListHandler.updateGroupToDoList(userGroup, toDoList);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update group to-do list for group: " + groupName, e);
        }
    }
}
