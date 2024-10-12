package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import core.User;
import persistence.GroupHandler;
import persistence.UserHandler;

/**
 * Controller class for adding a user to an existing group.
 * This class manages the process of retrieving users and assigning them to
 * selected groups in the application.
 */
public class AddUserToGroupController {

    @FXML
    private TextField username;

    @FXML
    private ListView<String> listViewOfGroups;

    @FXML
    private Label userNameErrorLabel;

    private User user;
    private List<String> groupNames = new ArrayList<>();
    private UserHandler userHandler = new UserHandler();
    private GroupHandler groupHandler = new GroupHandler();

    /**
     * Initializes the controller with the provided user and their groups.
     * Populates the ListView with the user's group names.
     *
     * @param user The current user whose groups are to be displayed.
     */
    public void initializeAddToUserGroup(User user) {
        this.user = user;
        this.groupNames = this.user.getUserGroups();
        for (String groupName : groupNames) {
            listViewOfGroups.getItems().add(groupName);
        }
    }

    /**
     * Handles the action of adding a user to the selected group.
     * Validates the username and group selection before proceeding with
     * the group assignment. Displays relevant messages for success or failure.
     */
    @FXML
    public void addUserToGroup() {
        // Clear previous error messages
        userNameErrorLabel.setText("");
        try {
            // Validate that the username field is not empty
            String inputUsername = username.getText().trim();
            if (inputUsername.isEmpty()) {
                userNameErrorLabel.setText("Username is empty");
                return;
            }

            // Check if the user exists
            if (!userHandler.userExists(inputUsername)) {
                userNameErrorLabel.setText("User does not exist");
                return;
            }

            // Retrieve the user from the UserHandler
            Optional<User> newUserOptional = userHandler.getUser(inputUsername);
            if (!newUserOptional.isPresent()) {
                userNameErrorLabel.setText("User retrieval failed");
                return;
            }

            User newUser = newUserOptional.get();

            // Validate that a group is selected
            String selectedGroupName = listViewOfGroups.getSelectionModel().getSelectedItem();
            if (selectedGroupName == null || selectedGroupName.isEmpty()) {
                userNameErrorLabel.setText("No group selected");
                return;
            }

            // Assign the user to the selected group
            try {
                groupHandler.assignUserToGroup(newUser, selectedGroupName);
                userNameErrorLabel.setText("User added to group successfully");
                userNameErrorLabel.setTextFill(Color.GREEN);
            } catch (Exception e) {
                e.printStackTrace();
                userNameErrorLabel.setText("Failed to add user to group");
            }

        } catch (Exception e) {
            // Handle any unforeseen exceptions
            e.printStackTrace();
            userNameErrorLabel.setText("An unexpected error occurred");
        }
    }
}
