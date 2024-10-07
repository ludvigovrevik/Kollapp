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
        Priority.getItems().addAll("High Priority", "Medium Priority", "Low Priority");
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
            ToDoListHandler.updateToDoList(user, toDoList);
            kollAppController.updateGrid();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    public void updateGrid() {
        // Clear grid view before retrieving tasks
        taskGridView.getChildren().clear();
        List<Task> tasks = toDoList.getTasks();
        
        // Iterate through all tasks
        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);
            String taskDescription = currentTask.getDescription();
            
            // check if date is empty
            Label dateLabel = new Label(""); 
            if (currentTask.getDateTime() != null) {
                LocalDate dateTime = currentTask.getDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                dateLabel.setText(dateTime.format(formatter));
            }
            Label taskLabel = new Label(taskDescription);
            
            CheckBox checkBox = new CheckBox();
            
            // Add components to the grid
            taskGridView.add(dateLabel, 0, i);
            taskGridView.add(taskLabel, 1, i);
            taskGridView.add(checkBox, 2, i);
        }
    }
}

