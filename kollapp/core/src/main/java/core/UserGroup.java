package core;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserGroup implements java.io.Serializable {
    private String groupName;
    private List<String> users = new ArrayList<>();
    private ToDoList toDoList = new ToDoList();

    // Default constructor (required for Jackson)
    public UserGroup() {}

    // Constructor for creating a new user group
    public UserGroup(String groupName) {
        this.groupName = groupName;
    }

    // Getter for groupName
    public String getGroupName() {
        return groupName;
    }

    // Setter for groupName
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // Getter for the list of users
    public List<String> getUsers() {
        return new ArrayList<>(users);  // Return a copy to avoid external modification
    }

    // Getter for to-do list
    public ToDoList getToDoList() {
        return toDoList;
    }

    // Setter for to-do list
    public void setToDoList(ToDoList toDoList) {
        if (toDoList == null) {
            throw new IllegalArgumentException("To-do list cannot be null.");
        }
        this.toDoList = toDoList;
    }

    // Returns the number of users in the group
    @JsonProperty 
    public int getNumberOfUsers() {
        return users.size();
    }

    // Adds a user to the group
    public void addUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (users.contains(username)) {
            throw new IllegalArgumentException("User already exists in the group.");
        }
        users.add(username);
    }

    // Removes a user from the group
    public void removeUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (!users.contains(username)) {
            throw new IllegalArgumentException("User does not exist in the group.");
        }
        users.remove(username);
    }

    // Checks if a user is in the group
    public boolean containsUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return users.contains(username);
    }
}
