package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import core.User;

@Component
public class UserHandler {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Path userPath;
    private final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Constructs a new UserHandler instance.
     * Initializes the paths for user data storage.
     * Registers the JavaTimeModule with the ObjectMapper to handle Java 8 date and time types.
     */
    @Autowired
    public UserHandler() {
        // Define the base path within the user's home directory
        String userHome = System.getProperty("user.home");
        this.userPath = Paths.get(userHome, ".kollapp", "users");
        
        // Register the JavaTimeModule to handle Java 8 date and time types
        this.mapper.registerModule(new JavaTimeModule());
        
        System.out.println("Initialized UserHandler with path: " + this.userPath.toAbsolutePath());
        
        // Ensure the directory exists
        File directory = this.userPath.toFile();
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Created directory: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }

    public UserHandler(String userPath) {
        // Define the base path within the user's home directory
        String userHome = System.getProperty("user.home");
        this.userPath = Paths.get(userHome, ".kollapp", "users");
        
        // Register the JavaTimeModule to handle Java 8 date and time types
        this.mapper.registerModule(new JavaTimeModule());
        
        System.out.println("Initialized UserHandler with path: " + this.userPath.toAbsolutePath());
        
        // Ensure the directory exists
        File directory = this.userPath.toFile();
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Created directory: " + directory.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }
    
    /**
     * Saves the given user to a file in JSON format.
     * 
     * @param user The user object to be saved.
     * @throws IllegalArgumentException if a user with the same username already exists.
     * @throws IOException if an I/O error occurs during saving.
     */
    public void saveUser(User user) throws IOException {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }
    
        // Hash the password before saving the user
        String hashedPassword = passwordEncoder.encode(user.getHashedPassword()); // Ensure this is the raw password
        User userWithHashedPassword = new User(user.getUsername(), hashedPassword);
    
        // Define the user file
        Path userFilePath = userPath.resolve(userWithHashedPassword.getUsername() + ".json");
        File userFile = userFilePath.toFile();
        System.out.println("Saving user to path: " + userFile.getAbsolutePath());
    
        try {
            mapper.writeValue(userFile, userWithHashedPassword);
            System.out.println("User saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save user: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Loads a user from a JSON file based on the provided username and password.
     *
     * @param username the username of the user to be loaded
     * @param password the password of the user to be loaded
     * @return an Optional containing the User if the username exists and the password matches; 
     *         Optional.empty() if the user does not exist or the password does not match
     */
    public Optional<User> loadUser(String username, String password) {
        Path userFilePath = userPath.resolve(username + ".json");
        File file = userFilePath.toFile();
        
        if (!userExists(username)) {
            return Optional.empty();
        }
    
        try {
            User user = mapper.readValue(file, User.class);
            
            // Check if the provided password matches the hashed password
            if (passwordEncoder.matches(password, user.getHashedPassword())) {
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            System.err.println("Failed to read user file: " + e.getMessage());
            throw new IllegalArgumentException("Failed to read user file", e);
        }
    }

    /**
     * Removes a user by their username.
     * <p>
     * This method checks if the user exists and then attempts to delete the user's
     * corresponding JSON file from the file system. If the file cannot be deleted,
     * a RuntimeException is thrown. (Will be used in the future to remove users from the system)
     * </p>
     *
     * @param username the username of the user to be removed
     * @throws RuntimeException if the user file cannot be deleted
     */
    public void removeUser(String username) {
        if (userExists(username)) {
            Path userFilePath = userPath.resolve(username + ".json");
            File file = userFilePath.toFile();
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    System.out.println("Deleted user file: " + file.getAbsolutePath());
                } else {
                    throw new RuntimeException("Failed to delete user file: " + file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Confirms if the provided user details are valid for creating a new user.
     *
     * @param username the username to be validated
     * @param password the password to be validated
     * @param confirmPassword the confirmation of the password to be validated
     * @return true if the username and passwords meet the required criteria, false otherwise
     * The criteria for a valid user are:
     * - None of the fields should be empty.
     * - The username must be at least 3 characters long.
     * - The password and confirmPassword must match.
     * - The password must be at least 6 characters long.
     */
    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        return username.length() >= 3
                && password.equals(confirmPassword)
                && password.length() >= 6;
    }

    /**
     * Validates the user input for creating a new user.
     *
     * @param username the username to be validated
     * @param password the password to be validated
     * @param confirmPassword the confirmation of the password to be validated
     * @return a validation error message if any validation rule is violated, or null if all inputs are valid
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
     * Assigns a group to a user.
     * Updates the user file with the new group added to the user's group list.
     *
     * @param username the username of the user
     * @param groupName the name of the group to assign
     * @throws RuntimeException if there is an error updating the user file
     */
    public void assignGroupToUser(String username, String groupName) {
        Optional<User> optionalUser = getUser(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.addUserGroup(groupName);
            updateUser(user);
        } else {
            throw new IllegalArgumentException("User does not exist: " + username);
        }
    }

    /**
     * Updates the user information in the corresponding JSON file.
     * If the user does not exist, an IllegalArgumentException is thrown.
     *
     * @param user The User object containing updated information.
     * @throws IllegalArgumentException if the user file does not exist.
     * @throws RuntimeException if there is an error writing to the user file.
     */
    private void updateUser(User user) {
        if (!userExists(user.getUsername())) {
            throw new IllegalArgumentException("User file does not exist for user: " + user.getUsername());
        }

        Path userFilePath = userPath.resolve(user.getUsername() + ".json");
        File file = userFilePath.toFile();
        try {
            mapper.writeValue(file, user);
            System.out.println("Updated user file: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to update user file for user: " + user.getUsername(), e);
        }
    }

    /**
     * Helper method. Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return an Optional containing the User if found, or an empty Optional if the user does not exist or an error occurs
     */
    public Optional<User> getUser(String username) {
        if (!userExists(username)) {
            return Optional.empty();
        }

        Path userFilePath = userPath.resolve(username + ".json");
        File file = userFilePath.toFile();
        System.out.println("Retrieving user from file: " + file.getAbsolutePath());
        try {
            User user = mapper.readValue(file, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            System.err.println("Failed to retrieve user: " + e.getMessage());
            throw new IllegalArgumentException("Failed to retrieve user", e);
        }
    }

    /**
     * Checks if a user exists by verifying the presence of a corresponding JSON file.
     *
     * @param username the username to check for existence
     * @return true if the user file exists, false otherwise
     */
    public boolean userExists(String username) {
        Path userFilePath = userPath.resolve(username + ".json");
        File file = userFilePath.toFile();
        return file.exists();
    }
}