package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import persistence.GroupHandler;
import persistence.ToDoListHandler;

public class AddUserToGroupController {
    private List<String> groupNames = new ArrayList<>();

    // TODO: Textfield

    // 

    @FXML
    ListView<UserGroup> listViewOfGroups;

    @FXML
    public void addUserToGroup() {
        // get selected groupNames
        // get username of text field
        // check if it is a valid user

        // for each selected groupname: assign user
            GroupHandler.assignUserToGroup(user, groupName);
        System.out.println("Added user to group");
    }

    


}
