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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import persistence.ToDoListHandler;

public class AddNewTaskController {
    @FXML
    private ComboBox<String> Priority;

    @FXML
    private TextField taskInputField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private GridPane taskGridView;

    private ToDoList toDoList;

    private User user;

    private KollAppController kollAppController;

    @FXML
    private Button handleAddTask;

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
        if (!taskInputField.getText().isEmpty()) {
            String taskDescription = taskInputField.getText();
            LocalDate dateTime = datePicker.getValue();
            Task newTask = new Task(taskDescription, dateTime);
            
            toDoList.addTask(newTask);
            taskInputField.clear();
            datePicker.setValue(null);
            ToDoListHandler.updateToDoList(user, toDoList);
            // update grid
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
            
            // Add event listener to the CheckBox
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    // Remove the task when checkbox is selected
                    toDoList.removeTask(currentTask);
                    ToDoListHandler.updateToDoList(user, toDoList);
                    updateGrid();  // Refresh the grid
                }
            });
            
            // Add elements to the grid
            taskGridView.add(checkBox, 0, i);
            taskGridView.add(taskLabel, 1, i);
            taskGridView.add(dateLabel, 2, i);
        }
    }


}

