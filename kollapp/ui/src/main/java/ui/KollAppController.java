package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import persistence.ToDoListHandler;

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


    @FXML
    private Label completedLabel;

    @FXML
    public void initialize() {
        // Set the label to act like a button
        completedLabel.setOnMouseClicked(this::handleLabelClick);

        // Change the cursor to hand when hovering over the label
        completedLabel.setStyle("-fx-cursor: hand;");
    }

    // Handle the click event on the label Completed
    @FXML
    private void handleLabelClick(MouseEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompletedTasks.fxml"));
            Pane newView = loader.load();
            CompletedTasksController controller = loader.getController();
            controller.initializeToDoList(toDoList);
            controller.initializeUser(user);
            // Get the current stage (window)
            Stage stage = (Stage) completedLabel.getScene().getWindow();

            // Set the new scene
            Scene newScene = new Scene(newView);
            stage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    currentTask.setCompleted(true); // Set the task as completed when checkbox is selected
                    //toDoList.removeTask(currentTask); // Remove the task when checkbox is selected
                    ToDoListHandler.updateToDoList(user, toDoList);
                    updateGrid();  // Refresh the grid
                }
            });
            if (!currentTask.isCompleted()) {
                // Add elements to the grid
                taskGridView.add(checkBox, 0, i);
                taskGridView.add(taskLabel, 1, i);
                taskGridView.add(dateLabel, 2, i);
            }
            
        }
    }

    public void innitializeToDoList(User user) {
        this.toDoList = ToDoListHandler.loadToDoList(user);
        this.user = user;
        updateGrid();
    }

}

