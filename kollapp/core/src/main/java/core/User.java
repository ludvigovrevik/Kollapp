package core;

import java.util.ArrayList;
import java.util.List;

public class User implements java.io.Serializable {
    private String username;
    private String password;
    private List<String> userGroups = new ArrayList<>();

    // Default constructor required for Jackson
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getUserGroups() {
        return userGroups;
    }

    public void addUserGroup(String userGroup) {
        if (!(this.userGroups.contains(userGroup))) {
            this.userGroups.add(userGroup);
        }
    }
}

