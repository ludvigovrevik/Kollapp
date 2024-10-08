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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import persistence.GroupHandler;
import persistence.ToDoListHandler;
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
            
            UserHandler.updateUser(user);


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