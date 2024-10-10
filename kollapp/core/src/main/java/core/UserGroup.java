package core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserGroup implements java.io.Serializable {
    private String groupName;
    private List<String> users = new ArrayList<>();

    // Default constructor required for Jackson
    public UserGroup() {
    }

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
        return new ArrayList<>(users); // Return a copy to avoid external modification
    }

    // Getter for the number of users
    @JsonIgnore
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

    public boolean containsUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return users.contains(username);
    }
}
