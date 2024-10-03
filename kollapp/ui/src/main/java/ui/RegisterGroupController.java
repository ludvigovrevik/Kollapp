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

    User user;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    public void createGroup() {
        // Get text from input field
        String groupName = groupNameField.getText();
        if (!groupName.isEmpty()) {
            if (this.user != null) {
                GroupHandler.saveGroup(this.user, groupName);
            } else {
                System.out.println("No user found in createGroup"); // add a label instead here
            }

        } else {
            System.out.println("Group Name empty");
        }
        // Creates grouphandler and creates new group

        System.out.println("Group created");
    }

}