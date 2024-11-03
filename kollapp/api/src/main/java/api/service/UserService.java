package api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Optional;
import persistence.UserHandler;
import core.User;


@Service
public class UserService {
    private final UserHandler userHandler;

    @Autowired
    public UserService() {
        String userPath = Paths.get("Prosjektet", "gr2409", "kollapp", "persistence", "src", "main", "java",
                "persistence", "users") + File.separator;
        this.userHandler = new UserHandler(userPath);
    }

    public Optional<User> getUser(String username) {
        return userHandler.getUser(username);
    }

    public void saveUser(User user) {
        try {
            userHandler.saveUser(user);
        } catch (IOException e) {
            throw new IllegalArgumentException(user + " already exists");
        }
    }

    public void removeUser(String username) {
        try {
            userHandler.removeUser(username);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public void updateUser(String username) {
        try {
            User user = userHandler.getUser(username).orElseThrow(() -> new NoSuchElementException("User not found."));
            userHandler.updateUser(user);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    public Optional<User> loadUser(String username, String password) {
        return userHandler.loadUser(username, password);
    }

    public boolean userExists(String username) {
        return userHandler.userExists(username);
    }

    public boolean confirmNewValidUser(String username, String password, String confirmPassword) {
        return userHandler.confirmNewValidUser(username, password, confirmPassword);
    }
    
    public String getUserValidationErrorMessage(String username, String password, String confirmPassword) {
        return userHandler.getUserValidationErrorMessage(username, password, confirmPassword);
    }

    public void assignGroupToUser(String username, String groupName) {
        userHandler.assignGroupToUser(username, groupName);
    }
}
