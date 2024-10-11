package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system, containing a username, password, 
 * associated groups, and a to-do list.
 */
public class User implements java.io.Serializable {
    private String username;
    private String password;
    private List<String> userGroups = new ArrayList<>();
    private ToDoList toDoList;

    /**
     * Default constructor required for Jackson deserialization.
     */
    public User() {
    }

    /**
     * Constructs a new user with a username and password.
     * Initializes the user's to-do list.
     *
     * @param username the username of the user
     * @param password the password of the user
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.toDoList = new ToDoList();
    }

    /**
     * Returns the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns a list of the user's groups.
     *
     * @return a list of user groups
     */
    public List<String> getUserGroups() {
        return new ArrayList<>(userGroups);
    }

    /**
     * Adds a group to the user's list of groups, if not already present.
     *
     * @param userGroup the group to add
     */
    public void addUserGroup(String userGroup) {
        if (!this.userGroups.contains(userGroup)) {
            this.userGroups.add(userGroup);
        }
    }

    /**
     * Sets the user's to-do list by copying an existing ToDoList.
     *
     * @param toDoList the ToDoList to set
     */
    public void setToDoList(ToDoList toDoList) {
        this.toDoList = new ToDoList(toDoList);
    }

    /**
     * Returns a copy of the user's to-do list.
     *
     * @return the user's to-do list
     */
    public ToDoList getToDoList() {
        return new ToDoList(toDoList);
    }
}
