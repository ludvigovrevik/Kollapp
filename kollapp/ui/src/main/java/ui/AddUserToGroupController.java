package ui;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import core.User;
import persistence.GroupHandler;

/**
 * Controller class for adding a user to an existing group.
 * This class manages the process of retrieving users and assigning them to
 * selected groups in the application.
 */
public class AddUserToGroupController {

    @FXML
    private TextField usernameField;

    @FXML
    private ListView<String> groupsListView;

    @FXML
    private Label feedbackLabel;
    
    private final HttpClient httpClient;
    private final GroupHandler groupHandler;

    // Constants for Feedback Messages
    private static final String USERNAME_EMPTY_MSG = "Username is empty.";
    private static final String USER_NOT_EXIST_MSG = "User does not exist.";
    private static final String USER_RETRIEVAL_FAILED_MSG = "User retrieval failed.";
    private static final String NO_GROUP_SELECTED_MSG = "No group selected.";
    private static final String ADD_USER_SUCCESS_MSG = "User added to group successfully.";
    private static final String ADD_USER_FAILURE_MSG = "Failed to add user to group.";


    // Constructor with Dependency Injection for Testability
    public AddUserToGroupController() {
        this.httpClient = HttpClient.newHttpClient();
        this.groupHandler = new GroupHandler();
    }

    // Constructor with Dependency Injection for Testability
    public AddUserToGroupController(GroupHandler groupHandler) {
        this.httpClient = HttpClient.newHttpClient();
        this.groupHandler = groupHandler;
    }

    /**
     * Initializes the controller with the provided user and populates the groups list.
     *
     * @param user The current user whose groups are to be displayed.
     */
    public void initializeAddToUserGroup(User user) {
        populateGroupsList(user.getUserGroups());
    }

    /**
     * Populates the ListView with the user's group names.
     *
     * @param groups List of group names.
     */
    private void populateGroupsList(List<String> groups) {
        groupsListView.getItems().setAll(groups);
    }

    /**
     * Handles the action of adding a user to the selected group.
     * Validates the input and performs the group assignment.
     */
    @FXML
    private void handleAddUserToGroup() {
        clearFeedback();

        String usernameInput = usernameField.getText().trim();

        // Validate Username
        String validationError = validateUsername(usernameInput);
        if (validationError != null) {
            displayFeedback(validationError, Color.RED);
            return;
        }

        // Retrieve User
        Optional<User> optionalUser = getUser(usernameInput);
        if (optionalUser.isEmpty()) {
            displayFeedback(USER_RETRIEVAL_FAILED_MSG, Color.RED);
            return;
        }

        User userToAdd = optionalUser.get();

        // Validate Group Selection
        String selectedGroup = groupsListView.getSelectionModel().getSelectedItem();
        if (selectedGroup == null || selectedGroup.isEmpty()) {
            displayFeedback(NO_GROUP_SELECTED_MSG, Color.RED);
            return;
        }

        // Assign User to Group
        try {
            groupHandler.assignUserToGroup(userToAdd, selectedGroup);
            displayFeedback(ADD_USER_SUCCESS_MSG, Color.GREEN);
        } catch (Exception e) {
            displayFeedback(ADD_USER_FAILURE_MSG, Color.RED);
        }
    }

    /**
     * Validates the input username.
     *
     * @param username The username input.
     * @return An error message if validation fails; otherwise, null.
     */
    private String validateUsername(String username) {
        if (username.isEmpty()) {
            return USERNAME_EMPTY_MSG;
        }

        if (!userExists(username)) {
            return USER_NOT_EXIST_MSG;
        }

        return null;
    }

    /**
     * Clears any existing feedback messages.
     */
    private void clearFeedback() {
        feedbackLabel.setText("");
    }

    /**
     * Displays a feedback message with the specified color.
     *
     * @param message The message to display.
     * @param color   The color of the text.
     */
    private void displayFeedback(String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setTextFill(color);
    }

    /**
     * Checks if a user exists by making an HTTP GET request to the API.
     *
     * @param username The username to check.
     * @return true if the user exists; false otherwise.
     */
    protected boolean userExists(String username) {
        String url = "http://localhost:8080/api/v1/users/exists/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            if (response.statusCode() == 200) {
                return Boolean.parseBoolean(response.body().trim());
            } else {
                // Handle error responses as needed
                return false;
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a user by making an HTTP GET request to the API.
     *
     * @param username The username of the user to retrieve.
     * @return An Optional containing the User if found; otherwise, an empty Optional.
     */
    protected Optional<User> getUser(String username) {
        String url = "http://localhost:8080/api/v1/users/" + username;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                User user = objectMapper.readValue(responseBody, User.class);
                return Optional.of(user);
            } else {
                return Optional.empty();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
