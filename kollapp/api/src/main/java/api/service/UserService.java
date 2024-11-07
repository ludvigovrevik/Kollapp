package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Service class for managing users in the application.
 * Provides methods for saving, loading, removing, and updating users.
 * User data is stored as JSON files.
 * 
 * <p>Uses BCrypt for password encryption and Jackson for JSON handling.
 * Supports basic user validation and assignment of groups to users.</p>
 * 
 * @see User
 */
@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String userPath;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs a UserService with a default path for user data storage.
     */
    @Autowired
    public UserService() {
        this.userPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users") + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Constructs a UserService with a specified path for user data storage.
     * 
     * @param userPath the path where user data will be stored
     */
    public UserService(Path userPath) {
        this.userPath = userPath + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Saves a new user. Encrypts the password before saving.
     * 
     * @param user the User object to save
     * @throws IOException if there is an issue writing the file
     * @throws IllegalArgumentException if a user with the same username already exists
     */
    public void saveUser(User user) throws IOException {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }
        String hashedPassword = passwordEncoder.encode(user.getHashedPassword());
        User userWithHashedPassword = new User(user.getUsername(), hashedPassword);
        
        File file = new File(userPath + userWithHashedPassword.getUsername() + ".json");
        mapper.writeValue(file, userWithHashedPassword);
    }

    /**
     * Loads a user if the username and password match.
     * 
     * @param username the username to look up
     * @param password the password to verify
     * @return an Optional containing the User if authentication is successful, or an empty Optional otherwise
     */
    public Optional<User> loadUser(String username, String password) {
        File file = new File(userPath + username + ".json");
        if (!userExists(username)) {
            return Optional.empty();
        }

        try {
            User user = mapper.readValue(file, User.class);
            if (passwordEncoder.matches(password, user.getHashedPassword())) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read user file", e);
        }
    }

    /**
     * Removes a user file based on the username.
     * 
     * @param username the username of the user to remove
     * @throws RuntimeException if the file deletion fails
     */
    public void removeUser(String username) {
        if (userExists(username)) {
            File file = new File(userPath + username + ".json");
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("Failed to delete user file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Checks if the provided username and passwords meet the criteria for a new valid user.
     * 
     * @param username the username to validate
     * @param password the password to validate
     * @param confirmPassword the confirmation password
     * @return true if the criteria are met, false otherwise
     */
    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        return username.length() >= 3
                && password.equals(confirmPassword)
                && password.length() >= 6;
    }

    /**
     * Returns an error message if validation fails for a new user.
     * 
     * @param username the username to validate
     * @param password the password to validate
     * @param confirmPassword the confirmation password
     * @return a validation error message, or null if the input is valid
     */
    public String getUserValidationErrorMessage(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Fields cannot be empty";
        }
        if (userExists(username)) {
            return "User already exists";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters long";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters long";
        }
        return null;
    }

    /**
     * Assigns a group to a user and updates the user's data.
     * 
     * @param username the username of the user
     * @param groupName the group name to assign to the user
     * @throws IllegalArgumentException if the user is not found
     */
    public void assignGroupToUser(String username, String groupName) {
        User user = getUser(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.addUserGroup(groupName);
        updateUser(user);
    }

    /**
     * Updates a user's data file.
     * 
     * @param user the User object to update
     * @throws IllegalArgumentException if the user does not exist
     * @throws RuntimeException if updating the file fails
     */
    private void updateUser(User user) {
        if (!userExists(user.getUsername())) {
            throw new IllegalArgumentException("User file does not exist for user: " + user.getUsername());
        }

        File file = new File(userPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update user file for user: " + user.getUsername());
        }
    }

    /**
     * Retrieves a user based on the username.
     * 
     * @param username the username to look up
     * @return an Optional containing the User if found, or an empty Optional otherwise
     */
    public Optional<User> getUser(String username) {
        if (!userExists(username)) {
            return Optional.empty();
        }

        File file = new File(userPath + username + ".json");
        try {
            User user = mapper.readValue(file, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to retrieve user");
        }
    }

    /**
     * Checks if a user exists based on their username.
     * 
     * @param username the username to check
     * @return true if the user exists, false otherwise
     */
    public boolean userExists(String username) {
        File file = new File(userPath + username + ".json");
        return file.exists();
    }
}