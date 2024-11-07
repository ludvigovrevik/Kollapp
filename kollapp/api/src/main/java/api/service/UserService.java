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

@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String userPath;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserService() {
        this.userPath = Paths.get("..", "persistence", "src", "main", "java", "persistence", "users") + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public UserService(Path userPath) {
        this.userPath = userPath + File.separator;
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void saveUser(User user) throws IOException {
        if (userExists(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }
        String hashedPassword = passwordEncoder.encode(user.getHashedPassword());
        User userWithHashedPassword = new User(user.getUsername(), hashedPassword);
        
        File file = new File(userPath + userWithHashedPassword.getUsername() + ".json");
        mapper.writeValue(file, userWithHashedPassword);
    }

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

    public void removeUser(String username) {
        if (userExists(username)) {
            File file = new File(userPath + username + ".json");
            if (file.exists() && !file.delete()) {
                throw new RuntimeException("Failed to delete user file: " + file.getAbsolutePath());
            }
        }
    }

    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        return username.length() >= 3
                && password.equals(confirmPassword)
                && password.length() >= 6;
    }

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

    public void assignGroupToUser(String username, String groupName) {
        User user = getUser(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.addUserGroup(groupName);
        updateUser(user);
    }

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

    public boolean userExists(String username) {
        File file = new File(userPath + username + ".json");
        return file.exists();
    }
}