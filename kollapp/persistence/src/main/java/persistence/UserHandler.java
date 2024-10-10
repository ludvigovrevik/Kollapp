package persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import core.ToDoList;
import core.User;

public class UserHandler {
    private final String userPath; 
    private final String toDoListPath; 
    private ObjectMapper mapper = new ObjectMapper();
    
    public UserHandler() {
        this.userPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users").toString() + File.separator;
        this.toDoListPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public UserHandler(String userPath, String todolistPath) {
        this.userPath = userPath;
        this.toDoListPath = todolistPath;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void saveUser(User user) {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }

        File file = new File(userPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User loadUser(String username, String password) {
        File file = new File(userPath + username + ".json");
        if (!userExists(username)) {
            return null;
        }

        try {
            User user = mapper.readValue(file, User.class);
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeUser(String username) {
        if (userExists(username)) {
            File file = new File(userPath + username + ".json");
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Failed to delete user file: " + file.getAbsolutePath());
                }
            }
        }
    }

    // helper method to validate a new user
    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false;
        }
        if (username.length() < 4) {
            return false;
        }
        if (!password.equals(confirmPassword)) {
            return false;
        }
        if (password.length() < 8) {
            return false;
        }
        return true;
    }

    public String getUserValidationErrorMessage(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return "Fields cannot be empty";
        }
        if (userExists(username)) {
            return "User already exists";
        }
        if (username.length() < 4) {
            return "Username must be at least 4 characters long";
        }
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        return null;
    }

    public Optional<User> getUser(String username) {
        if (!userExists(username)) {
            return Optional.empty();
        }
        
        File file = new File(userPath + username + ".json");
        try {
            User user = mapper.readValue(file, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void updateUser(User user) {
        if (!userExists(user.getUsername())) {
            throw new IllegalArgumentException("User file does not exist for user: " + user.getUsername());
        }

        File file = new File(userPath + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user file for user: " + user.getUsername());
        }
    }

    // returns wheather or not a user exists in database
    public boolean userExists(String username) {
        File file = new File(userPath + username + ".json");
        return file.exists(); 
    }
}