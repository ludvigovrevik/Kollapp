package ui;

import core.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.UserHandler;

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
    private GroupHandler groupHandler;

    /**
     * Sets the user for the group registration process.
     *
     * @param user The user who is creating the group
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Initializes the controller with the current user and main application controller.
     *
     * @param user The user for whom the group is being created
     * @param kollAppController The main application controller responsible for updating the UI
     */
    public void initialize(User user, KollAppController kollAppController) {
        this.user = user;
        this.kollAppController = kollAppController;
        groupHandler = new GroupHandler();
    }

    /**
     * Handles the creation of a new group.
     * <p>
     * This method retrieves the group name from the input field and performs validation checks.
     * If the group name is valid and the user is logged in, it creates a new group and updates the user.
     * The UI is then updated to reflect the new group. If any errors occur during the process,
     * an appropriate error message is displayed.
     * </p>
     * 
     * @throws Exception if an unexpected error occurs during group creation
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
            groupHandler.createGroup(this.user, groupName);
            kollAppController.populateGroupView();

            // Close the current window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            errorLabel.setText("Failed to create group. Please try again.");
            e.printStackTrace();
        }
    }

    /**
     * Confirms if the provided group details are valid for creating a new group.
     *
     * @param groupName the group name to be validated
     * @return true if the group name meets the required criteria, false otherwise
     * 
     * The criteria for a valid group are:
     * - The group name must not be empty.
     * - The group must not already exist.
     */
    public boolean confirmNewValidGroup(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return false;
        }
        if (groupHandler.groupExists(groupName)) {
            return false;
        }
        return true;
    }

    /**
     * Provides a validation error message for creating a new group.
     *
     * @param groupName the group name to be validated
     * @return a validation error message if any validation rule is violated, or null if the group name is valid
     */
    public String getGroupValidationErrorMessage(String groupName) {
        if (groupName == null || groupName.isEmpty()) {
            return "Group Name cannot be empty";
        }
        if (groupHandler.groupExists(groupName)) {
            return "Group already exists";
        }
        return null;
    }
}
