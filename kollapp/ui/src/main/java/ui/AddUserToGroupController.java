package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import persistence.GroupHandler;
import persistence.ToDoListHandler;
import persistence.UserHandler;
import java.util.NoSuchElementException;

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
        
        String selectedGroupName = listViewOfGroups.getSelectionModel().getSelectedItem();
        System.out.println("Før bruker er hentet");
        Optional<User> newuser = UserHandler.getUser(username.getText());
        System.out.println("etter: " + newuser.get().getUsername());
        GroupHandler.assignUserToGroup(newuser.get(), selectedGroupName);
    }   
}
