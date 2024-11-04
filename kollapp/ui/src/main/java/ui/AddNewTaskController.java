package ui;

import java.time.LocalDate;

import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import api.ToDoListApiHandler;

/**
 * Controller class for managing the addition of new tasks to a to-do list.
 * This class allows users to specify task details such as name, description, 
 * due date, and priority, and then add the task to their personal or group 
 * to-do list.
 */
public class AddNewTaskController {
    @FXML
    private ComboBox<String> priorityField;

    @FXML
    public Label errorLabel;

    @FXML
    private TextField taskNameField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea taskDescriptionField;
    
    private ToDoList toDoList;
    private ToDoListApiHandler toDoListApiHandler;
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
        this.toDoListApiHandler = new ToDoListApiHandler();
        priorityField.getItems().addAll(Task.PRIORITY_NAMES);
    }

    /**
     * Handles the action of adding a new task to the to-do list.
     * Collects the task details (name, description, date, priority) entered by the user,
     * creates a new task, and updates the to-do list accordingly. Depending on the context,
     * the task is added either to the group’s to-do list or to the user's personal list.
     * After updating, the view is refreshed, and the window is closed.
     *
     * @param event The event triggered by clicking the "Add Task" button.
     */
    @FXML
    public void handleAddTask(ActionEvent event) {
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
                toDoListApiHandler.updateGroupToDoList(groupInView, toDoList);
                System.out.println("Updated to-do list for group: " + groupInView.getGroupName());
            } else {
                // Add task to the user's personal to-do list
                toDoListApiHandler.updateToDoList(user, toDoList);
                System.out.println("Updated to-do list for user: " + user.getUsername());
            }
            kollAppController.updateTableView();

            // Close the current window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Task cannot be null.");
        }
    }
}

    



