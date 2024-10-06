package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import persistence.GroupHandler;
import persistence.ToDoListHandler;

public class RegisterGroupController {
    @FXML
    TextField groupNameField;

    @FXML
    Label errorLabel;

    User user;

    public void setUser(User user) {
        this.user = user;
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
            // Attempt to save the group
            GroupHandler.saveGroup(this.user, groupName);
        } catch (Exception e) {
            // Handle any unexpected exceptions
            errorLabel.setText("Failed to create group. Please try again.");
            e.printStackTrace();
        }
}

}