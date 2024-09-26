package core;

import java.util.ArrayList;
import java.util.List;

public class UserGroup {
    private List<User> users;

    /**
     * Constructs a new UserGroup with an empty list of users.
     */
    public UserGroup() {
        this.users = new ArrayList<>(); 
    }

    /**
     * Retrieves the list of users in the user group.
     *
     * @return a list of User objects representing the users in the group.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Checks if the specified user is part of the user group.
     *
     * @param user the user to check for membership in the group
     * @return true if the user is part of the group, false otherwise
     * @throws IllegalArgumentException if the user is null
     */
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        users.add(user);
    }

    /**
     * Removes a user from the user group.
     *
     * @param user the user to be removed
     * @throws IllegalArgumentException if the user is null or does not exist in the group
     */
    public void removeUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!users.contains(user)) {
            throw new IllegalArgumentException("User does not exist");
        }
        users.remove(user);
    }
}
