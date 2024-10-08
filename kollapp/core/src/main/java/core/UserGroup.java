package core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class UserGroup implements java.io.Serializable {
    private String groupName;
    private List<String> users = new ArrayList<>();
    private int numberOfUsers;

    // Default constructor required for Jackson
    public UserGroup() {
    }

    public UserGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public List<String> getUsers() {
        return users;
    }

    @JsonProperty 
    public int getNumberOfUsers() {
        this.numberOfUsers = users.size();
        return users.size();
    }

    public void addUser(String username) {
        if (users.contains(username)) {
            throw new IllegalArgumentException("User already exists in the group.");
        }
        users.add(username);
    }

    public void removeUser(String username) {
        if (!users.contains(username)) {
            throw new IllegalArgumentException("User does not exist in the group.");
        }
        users.remove(username);
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @JsonIgnore
    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public boolean containsUser(String username) {
        return users.contains(username);
    }
}
