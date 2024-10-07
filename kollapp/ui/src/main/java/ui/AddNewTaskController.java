package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import core.Task;
import core.ToDoList;
import core.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import persistence.ToDoListHandler;

public class AddNewTaskController {
    @FXML
    private ComboBox<String> Priority;

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
    private User user;
    private KollAppController kollAppController;

    @FXML
    public void initializeTaskWindow(User user, ToDoList toDoList, KollAppController kollAppController) {
        this.toDoList = toDoList;
        this.user = user;
        this.kollAppController = kollAppController;
        // Initialize the ComboBox with some items
        Priority.getItems().addAll(Task.PRIORITY_NAMES);
    }

    @FXML
    public void handleAddTask(ActionEvent event) {
        if (!taskNameField.getText().isEmpty()) {
            String taskName = taskNameField.getText();
            LocalDate dateTime = datePicker.getValue();
            String description = taskDescriptionField.getText();
            String priority = Priority.getValue();
            
            Task newTask = new Task(taskName, dateTime, description, priority);
            
            toDoList.addTask(newTask);
            ToDoListHandler handler = new ToDoListHandler();
            handler.updateToDoList(user, toDoList);
            kollAppController.updateGrid();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }
}

