package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import core.User;
import persistence.GroupHandler;
import persistence.UserHandler;

public class AddUserToGroupController {
    private User user;
    private List<String> groupNames = new ArrayList<>();

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
    public void addUserToGroup() {
        // Get selectedGroupName
        String selectedGroupName = listViewOfGroups.getSelectionModel().getSelectedItem();
        // Get user from username in text-field
        Optional<User> newuser = UserHandler.getUser(username.getText());
        // Assign user to selected group
        GroupHandler.assignUserToGroup(newuser.get(), selectedGroupName);
    }   
}
