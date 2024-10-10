package ui;

import java.io.IOException;
import java.time.LocalDate;

import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import persistence.ToDoListHandler;

public class AddNewTaskController {
    @FXML
    private ComboBox<String> priorityField;

    @FXML
    private TextField taskNameField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private GridPane taskGridView;

    @FXML
    private Button handleAddTask;

    @FXML
    private TextArea taskDescriptionField;
    
    private ToDoList toDoList;
    private ToDoListHandler toDoListHandler;
    private User user;
    private KollAppController kollAppController;

    /**
     * Initializes the task window with the provided user, to-do list, and main controller.
     * Sets up the to-do list handler and populates the priority field with priority names.
     *
     * @param user The user associated with the task window.
     * @param toDoList The to-do list to be managed in the task window.
     * @param kollAppController The main controller of the application.
     */
    @FXML
    public void initializeTaskWindow(User user, ToDoList toDoList, KollAppController kollAppController) {
        this.toDoList = toDoList;
        this.user = user;
        this.kollAppController = kollAppController;
        this.toDoListHandler = new ToDoListHandler();
        priorityField.getItems().addAll(Task.PRIORITY_NAMES);
    }

    @FXML
    public void handleAddTask(ActionEvent event) throws IOException {
        if (!taskNameField.getText().isEmpty()) {
            String taskName = taskNameField.getText();
            LocalDate dateTime = datePicker.getValue();
            String description = taskDescriptionField.getText();
            String priority = priorityField.getValue();
            
            Task newTask = new Task(taskName, dateTime, description, priority);

            toDoList.addTask(newTask);
            
            UserGroup groupInView = kollAppController.getGroupInView(); 
            if (groupInView != null) {
                // Add task to the group's to-do list
                toDoListHandler.updateGroupToDoList(groupInView, toDoList);
                System.out.println("Updated to-do list for group: " + groupInView.getGroupName());
            } else {
                // Add task to the user's personal to-do list
                toDoListHandler.updateToDoList(user, toDoList);
                System.out.println("Updated to-do list for user: " + user.getUsername());
            }
            kollAppController.updateGrid();

            // Close the current window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    } 
}

    



