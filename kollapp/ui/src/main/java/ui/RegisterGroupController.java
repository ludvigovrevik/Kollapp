package ui;

import api.GroupApiHandler;
import api.UserApiHandler;
import core.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import persistence.GroupChatHandler;

/**
 * Controller class for handling the creation of new user groups.
 * This class manages group creation by validating input, updating user data,
 * and refreshing the UI to reflect the changes.
 */
public class RegisterGroupController {

    @FXML
    private TextField groupNameField;

    @FXML
    private Label errorLabel;

    private User user;
    private KollAppController kollAppController;
    private GroupChatHandler groupChatHandler;
    private GroupApiHandler groupApiHandler;
    private UserApiHandler userApiHandler;

    /**
     * Sets the user for the group registration process.
     *
     * @param user The user who is creating the group
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Initializes the controller with the current user and main application
     * controller.
     *
     * @param user              The user for whom the group is being created
     * @param kollAppController The main application controller responsible for
     *                          updating the UI
     */
    public void initialize(User user, KollAppController kollAppController) {
        this.user = user;
        this.kollAppController = kollAppController;
        this.groupChatHandler = new GroupChatHandler();
        this.groupApiHandler = new GroupApiHandler();
        this.userApiHandler = new UserApiHandler();
    }

    /**
     * Setter for KollAppController to allow injection of mock in tests.
     *
     * @param kollAppController The mocked KollAppController
     */
    public void setKollAppController(KollAppController kollAppController) {
        this.kollAppController = kollAppController;
    }

    /**
     * Setter for GroupApiHandler to allow injection of mock in tests.
     *
     * @param groupApiHandler The mocked GroupApiHandler
     */
    public void setGroupApiHandler(GroupApiHandler groupApiHandler) {
        this.groupApiHandler = groupApiHandler;
    }

    /**
     * Setter for UserApiHandler to allow injection of mock in tests.
     *
     * @param userApiHandler The mocked UserApiHandler
     */
    public void setUserApiHandler(UserApiHandler userApiHandler) {
        this.userApiHandler = userApiHandler;
    }

    /**
     * Handles the creation of a new group.
     * <p>
     * This method retrieves the group name from the input field and performs
     * validation checks.
     * If the group name is valid and the user is logged in, it creates a new group
     * and updates the user.
     * The UI is then updated to reflect the new group. If any errors occur during
     * the process,
     * an appropriate error message is displayed.
     * </p>
     */
    @FXML
    public void createGroup(ActionEvent event) {
        String groupName = groupNameField.getText();
        if (!confirmNewValidGroup(groupName)) {
            String errorMessage = getGroupValidationErrorMessage(groupName);
            errorLabel.setText(errorMessage);
            return;
        }

        try {
            boolean groupCreated = groupApiHandler.createGroup(this.user.getUsername(), groupName);
            if (!groupCreated) {
                errorLabel.setText("Failed to create group. Please try again.");
                return;
            }

            boolean userAssigned = userApiHandler.assignGroupToUser(this.user.getUsername(), groupName);
            if (!userAssigned) {
                errorLabel.setText("Failed to assign user to group. Please try again.");
                return;
            }

            user.addUserGroup(groupName);
            kollAppController.populateGroupView(user.getUserGroups());

            // Close the current window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            groupChatHandler.createGroupChat(groupName);

        } catch (Exception e) {
            errorLabel.setText("An unexpected error occurred. Please try again.");
        }
    }


    /**
     * Confirms if the provided group details are valid for creating a new group.
     *
     * @param groupName the group name to be validated
     * @return true if the group name meets the required criteria, false otherwise
     *         The criteria for a valid group are:
     *         - The group name must not be empty.
     *         - The group must not already exist.
     */
    public boolean confirmNewValidGroup(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return false;
        }
        return !groupApiHandler.groupExists(groupName);
    }

    /**
     * Provides a validation error message for creating a new group.
     *
     * @param groupName the group name to be validated
     * @return a validation error message if any validation rule is violated, or
     *         null if the group name is valid
     */
    public String getGroupValidationErrorMessage(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return "Group Name cannot be empty";
        }
        if (groupApiHandler.groupExists(groupName)) {
            return "Group already exists";
        }
        return null;
    }
}