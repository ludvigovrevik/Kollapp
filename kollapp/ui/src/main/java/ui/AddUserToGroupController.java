package ui;

import java.util.List;

import api.GroupApiHandler;
import api.UserApiHandler;
import core.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

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

    private UserApiHandler userApiHandler;
    private GroupApiHandler groupApiHandler;

    // Constructor with Dependency Injection for Testability
    public AddUserToGroupController() {
        this.userApiHandler = new UserApiHandler();
        this.groupApiHandler = new GroupApiHandler();
    }

    protected void setUserApiHandler(UserApiHandler userApiHandler) {
        this.userApiHandler = userApiHandler;
    }

    protected void setGroupApiHandler(GroupApiHandler groupApiHandler) {
        this.groupApiHandler = groupApiHandler;
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

    @FXML
    public void handleAddUserToGroup() {
        String username = usernameField.getText();
        String selectedGroup = groupsListView.getSelectionModel().getSelectedItem();

        // First validate basic inputs
        if (!validateInputs(username, selectedGroup)) {
            return;
        }

        try {
            // Validate the group assignment
            String validationError = groupApiHandler.validateGroupAssignment(username, selectedGroup);
            if (validationError != null && !validationError.isEmpty()) {  // Check for both null and empty
                setFeedback(validationError, true);
                System.out.println("Validation failed: " + validationError);
                return;
            }

            boolean groupAssigningSuccessful = groupApiHandler.assignUserToGroup(username, selectedGroup);
            boolean userAssigningSuccessful = userApiHandler.assignGroupToUser(username, selectedGroup);

            if (groupAssigningSuccessful && userAssigningSuccessful) {
                setFeedback("User added successfully", false);
            } else {
                setFeedback("Failed to add user to group.", true);
            }
        } catch (Exception e) {
            String errorMessage = "Failed to add user to group: " + e.getMessage();
            setFeedback(errorMessage, true);
            System.out.println(errorMessage);
        }
    }

    private boolean validateInputs(String username, String selectedGroup) {
        if (username == null || username.trim().isEmpty()) {
            setFeedback("Username is empty.", true);
            return false;
        }

        if (selectedGroup == null) {
            setFeedback("No group selected.", true);
            return false;
        }

        return true;
    }

    private void setFeedback(String message, boolean isError) {
        feedbackLabel.setText(message);
        feedbackLabel.setTextFill(isError ? Color.RED : Color.GREEN);
    }
}
