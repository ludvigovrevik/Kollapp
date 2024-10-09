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

    public void setUser(User user) {
        this.user = user;
    }

    public void initialize(User user, KollAppController kollAppController) {
        this.user = user;
        this.kollAppController = kollAppController; 
    }
    
    @FXML
    public void createGroup() {
        try {
            // Get text from input field
            String groupName = groupNameField.getText();

            if (groupName == null || groupName.isEmpty()) {
                errorLabel.setText("Group Name cannot be empty");
                return;
            } else if (this.user == null) {
                errorLabel.setText("User not found. Please log in.");
                return;
            }
            // Attempt to create the group
            System.out.println("before: " + this.user.getUserGroups().toString());
            GroupHandler.createGroup(this.user, groupName);
            System.out.println("after: " + this.user.getUserGroups().toString());
            
            UserHandler userHandler = new UserHandler();
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