package core;

import java.util.ArrayList;
import java.util.List;

public class User implements java.io.Serializable {
    private String username;
    private String password;
    private List<String> userGroups = new ArrayList<>();

    // Default constructor required for Jackson
    public User() {}

    // Constructor for creating a new user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Get the username of the user
    public String getUsername() {
        return username;
    }

    // Get the password of the user
    public String getPassword() {
        return password;
    }

    // Get the list of user groups
    public List<String> getUserGroups() {
        return userGroups;
    }

    // Add a group to the users list of groups
    public void addUserGroup(String userGroup) {
        if (!(this.userGroups.contains(userGroup))) {
            this.userGroups.add(userGroup);
        }
    }
}

