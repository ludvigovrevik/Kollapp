package ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import core.Task;
import core.ToDoList;
import core.User;
import persistence.ToDoListHandler;


import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    private User user;

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
            ToDoListHandler.updateToDoList(user, toDoList);
            // update grid 
            updateGrid();
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

    public void innitializeToDoList(User user) {
        this.toDoList = ToDoListHandler.loadToDoList(user);
        this.user = user;
        updateGrid();
    }


@FXML
    public void showDialog() {
        try{
            // Load the FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddNewTask.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new stage for the dialog
            Stage stage = new Stage();
            stage.setTitle("Add New Task");

            // Set the scene for the FXML dialog file
            stage.setScene(new Scene(root));

            // Set the stage as modal, so it blocks user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
 
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }


}

