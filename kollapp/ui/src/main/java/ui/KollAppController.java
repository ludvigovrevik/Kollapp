package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import core.Task;
import core.ToDoList;

/**
 * Controller class for the KollApp application.
 */
public class KollAppController {

    @FXML
    private GridPane taskGridView;

    @FXML
    private TextField taskInputField;

    @FXML 
    private DatePicker datePicker;

    private ToDoList toDoList;

    /**
     * Initializes the controller class.
     * This method is called automatically after the FXML file has been loaded.
     * It creates a new to-do list and sets up the ListView to display the tasks.
     */
    public void initialize() {
    toDoList = new ToDoList();

        
        updateGrid();
        
    }

    /**
     * Handles the action of adding a new task.
     * This method is called when the user clicks the "+" button from the UI.
     * It retrieves the task description and date from the input fields and creates a new Task object.
     * The new task is then added to the to-do list, and clears the input fields.
     */
    @FXML
    public void handleAddTask() {
        if (!taskInputField.getText().isEmpty()) {
            String taskDescription = taskInputField.getText();
            LocalDate dateTime = datePicker.getValue();
            Task newTask = new Task(taskDescription, dateTime);
            
            toDoList.addTask(newTask);
            taskInputField.clear();
            datePicker.setValue(null);

            // update grid 
            updateGrid();
        }
    }

    @FXML
    public void updateGrid() {
        // Clear grid view before retrieving tasks
        taskGridView.getChildren().clear();
        ObservableList<Task> tasks = toDoList.getTasks();
        
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
                    updateGrid();  // Refresh the grid
                }
            });
            
            // Add elements to the grid
            taskGridView.add(checkBox, 0, i);
            taskGridView.add(taskLabel, 1, i);
            taskGridView.add(dateLabel, 2, i);
        }
    } 



    /**
     * Handles the action of removing a task.
     * This method is called when the "-" button from the UI is clicked.
     * It removes the selected task from the to-do list.
     */
    
        // Check if a task is selected before removing it.
        if (selectedIndex >= 0) {
            toDoList.removeTask(selectedIndex);
        } else {
            System.out.println("No task selected.");
        }
    }

    // TODO implement setUser method
    public void setUser(User user) {
    }
}
