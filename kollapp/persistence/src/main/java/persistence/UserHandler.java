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
    private final String USER_PATH; 
    private final String TODOLIST_PATH; 
    private ObjectMapper mapper = new ObjectMapper();
    
    public UserHandler() {
        this.USER_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users").toString() + File.separator;
        this.TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public UserHandler(String userPath, String todolistPath) {
        this.USER_PATH = userPath;
        this.TODOLIST_PATH = todolistPath;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void saveUser(User user) {
        File file = new File(USER_PATH + user.getUsername() + ".json");
        if (file.exists()) {
            throw new IllegalArgumentException("User already exists");
        }
        try {
            mapper.writeValue(file, user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User loadUser(String username, String password) {
        File file = new File(USER_PATH + username + ".json");
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

    // returns wheather or not a user exists in database
    public boolean userExists(String username) {
        File file = new File(USER_PATH + username + ".json");
        return file.exists(); 
    }

    public Optional<User> getUser(String username) {
        File file = new File(USER_PATH + username + ".json");
        try {
            User user = mapper.readValue(file, User.class);
            return Optional.of(user);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void updateUser(User user) {
        File file = new File(USER_PATH + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update user file for user: " + user.getUsername());
        }
    }

}