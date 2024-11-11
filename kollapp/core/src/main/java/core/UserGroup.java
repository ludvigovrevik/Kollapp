package core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of users, containing the group's name and a list of users.
 */
public class UserGroup implements Serializable {
    private String groupName;
    private List<String> users = new ArrayList<>();

    /**
     * Default constructor required for Jackson deserialization.
     */
    public UserGroup() {
    }

    /**
     * Constructs a new UserGroup with the specified group name.
     *
     * @param groupName the name of the group
     */
    public UserGroup(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Returns the name of the group.
     *
     * @return the group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the name of the group.
     *
     * @param groupName the name to set
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Returns a copy of the list of users in the group.
     *
     * @return a list of users
     */
    public List<String> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Sets the list of users for the UserGroup.
     *
     * @param users the list of user names to be set
     */
    public void setUsers(List<String> users) {
        this.users = new ArrayList<>(users);
    }

    /**
     * Adds a user to the group if the user is not already present.
     *
     * @param username the username to add
     * @throws IllegalArgumentException if the username is null, empty, or already exists in the group
     */
    public void addUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (users.contains(username)) {
            throw new IllegalArgumentException("User already exists in the group.");
        }
        users.add(username);
    }

    /**
     * Removes a user from the group if the user exists.
     *
     * @param username the username to remove
     * @throws IllegalArgumentException if the username is null, empty, or does not exist in the group
     */
    public void removeUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (!users.contains(username)) {
            throw new IllegalArgumentException("User does not exist in the group.");
        }
        users.remove(username);
    }

    /**
     * Checks if the group contains the specified user.
     *
     * @param username the username to check
     * @return true if the user is in the group, false otherwise
     * @throws IllegalArgumentException if the username is null or empty
     */
    public boolean containsUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        return users.contains(username);
    }
}
