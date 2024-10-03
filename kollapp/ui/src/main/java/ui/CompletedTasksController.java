package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import javafx.scene.layout.GridPane;
import persistence.ToDoListHandler;
import core.Task;
import core.ToDoList;
import core.User;

public class CompletedTasksController {

    @FXML
    private GridPane taskGridView;

    private ToDoList toDoList;

    private User user;

    public void initializeToDoList(ToDoList toDoList) {

        this.toDoList = toDoList;

        updateGrid();
    }

    public void initializeUser(User user) {

        this.user = user;
        
        updateGrid();
    }
    
    @FXML
    public void updateGrid() {
        // Clear grid view before retrieving tasks
        if (taskGridView.getChildren().size() > 0 || taskGridView.getChildren() != null) {
            taskGridView.getChildren().clear();
        }

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
                    // currentTask.setCompleted(true); // Set the task as completed when checkbox is selected
                    toDoList.removeTask(currentTask); // Remove the task when checkbox is selected
                    ToDoListHandler.updateToDoList(user, toDoList);
                    updateGrid();  // Refresh the grid
                }
            });

            // Only tasks that are completed are shown in the completed tasks view
            if (currentTask.isCompleted()) {
                // Add elements to the grid
                taskGridView.add(checkBox, 0, i);
                taskGridView.add(taskLabel, 1, i);
                taskGridView.add(dateLabel, 2, i);
            }
            
        }
    }
    
}
