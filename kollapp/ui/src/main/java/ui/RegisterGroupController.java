package ui;

import core.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import persistence.GroupHandler;
import persistence.UserHandler;

public class RegisterGroupController {
    @FXML
    private TextField groupNameField;

    @FXML
    private Label errorLabel;

    private User user;
    private KollAppController kollAppController;
    private UserHandler userHandler;
    private GroupHandler groupHandler;


    public void setUser(User user) {
        this.user = user;
    }

    public void initialize(User user, KollAppController kollAppController) {
        this.user = user;
        this.kollAppController = kollAppController;
        userHandler = new UserHandler();
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
     * <p><b>Note:</b> Ensure that the user is logged in before attempting to create a group.</p>
     * 
     * @throws Exception if an unexpected error occurs during group creation.
     */
    @FXML
    public void createGroup() {
        try {
            String groupName = groupNameField.getText();

            if (groupName == null || groupName.isEmpty()) {
                errorLabel.setText("Group Name cannot be empty");
                return;
            } else if (this.user == null) {
                errorLabel.setText("User not found. Please log in.");
                return;
            }
            // create group file and update user file
            groupHandler.createGroup(this.user, groupName);
            userHandler.updateUser(user);

            // update Kollapp ui
            kollAppController.populateGroupView();

            // testing and here is the mistake
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Made group succesfully");
            
        } catch (Exception e) {
            // Handle any unexpected exceptions
            errorLabel.setText("Failed to create group. Please try again.");
            e.printStackTrace();
        }
    }
}