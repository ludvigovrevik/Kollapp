package core;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a collection of users. 
 */
public class UserManager {
    private Map<String, User> users;

    /**
     * Constructs a new Users collection.
     */
    public UserManager() {
        this.users = new HashMap<>();
    }

    /**
     * Adds a user to the collection.
     *
     * @param user the user to add
     * @throws IllegalArgumentException if a user with the same username already exists or if the user is null
     */
    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (users.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("User with that username already exists");
        }
        users.put(user.getUsername(), user);
    }

    /**
     * Checks if a user with the specified username exists.
     *
     * @param username the username to check
     * @return true if a user with the specified username exists, false otherwise
     */
    private boolean containsUser(String username) {
        return users.containsKey(username);
    }

    /**
     * Returns the user with the specified username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the user with the specified username and password, or null if no such user exists
     */
    public User getUser(String username, String password) {
        if (containsUser(username)) {
            User user = users.get(username);
            if (user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }
}
