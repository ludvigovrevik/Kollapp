package core;

import java.util.ArrayList;
import java.util.List;


public class UserGroup implements java.io.Serializable {
    private String groupName;
    private List<String> users = new ArrayList<>();
    private ToDoList toDoList = new ToDoList();
    

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

    public ToDoList getToDoList() {
        return toDoList;
    }
    
    public int getNumberOfUsers() {
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

    public void setToDoList(ToDoList toDoList) {
        this.toDoList = toDoList;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean containsUser(String username) {
        return users.contains(username);
    }

   
}
