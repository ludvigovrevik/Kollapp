package core;

public class User {
    private String username;
    private String password;

    /**
     * Constructs a new User with the specified username and password.
     *
     * @param username the username for the user, must be at least 4 characters long
     * @param password the password for the user, must be at least 8 characters long
     * @throws IllegalArgumentException if the username is less than 4 characters or the password is less than 8 characters
     */
    public User(String username, String password) {
        if (password.length() <= 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if (username.length() <= 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters long");
        }
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    
    /**
     * Checks if the provided password matches the user's password.
     *
     * @param password the password to check
     * @return true if the provided password matches the user's password, false otherwise
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

}

