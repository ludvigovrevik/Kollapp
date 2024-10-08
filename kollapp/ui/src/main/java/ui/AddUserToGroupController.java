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

public class AddUserToGroupController {
    private User user;
    private List<String> groupNames = new ArrayList<>();
    UserHandler userHandler = new UserHandler();

    public void initializeAddToUserGroup(User user) {
        this.user = user;
        this.groupNames = this.user.getUserGroups();
        for (String groupname : groupNames) {
            listViewOfGroups.getItems().add(groupname);
        }
    }

    @FXML
    TextField username;

    @FXML
    ListView<String> listViewOfGroups;

    @FXML
    Label userNameErrorLabel;

    @FXML
    public void addUserToGroup() {
    // Clear previous error messages
        userNameErrorLabel.setText("");
        try {
            // Validate that the username field is not empty
            String inputUsername = username.getText().trim();
            if (inputUsername.isEmpty()) {
                userNameErrorLabel.setText("Username is empty");
                return; // Exit the method early
            }

            // Check if the user exists

            if (userHandler.userExists(inputUsername)) {
                userNameErrorLabel.setText("User does not exist");
                return; // Exit the method early
            }

            // Retrieve the user from the UserHandler
            Optional<User> newUserOptional = userHandler.getUser(inputUsername);
            if (!newUserOptional.isPresent()) {
                userNameErrorLabel.setText("User retrieval failed");
                return; // Exit the method early
            }

            User newUser = newUserOptional.get();

            // Validate that a group is selected
            String selectedGroupName = listViewOfGroups.getSelectionModel().getSelectedItem();
            if (selectedGroupName == null || selectedGroupName.isEmpty()) {
                userNameErrorLabel.setText("No group selected");
                return; // Exit the method early
            }

            // Assign the user to the selected group within a try-catch block
            try {
                GroupHandler.assignUserToGroup(newUser, selectedGroupName);
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
