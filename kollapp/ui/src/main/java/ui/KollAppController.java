package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import core.Task;
import core.ToDoList;
import core.User;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    public void handleAddTasko() {
        if (!taskInputField.getText().isEmpty()) {
            String taskDescription = taskInputField.getText();
            LocalDate dateTime = datePicker.getValue();

            Task newTask = new Task();
            newTask.setDescription(taskDescription);

            toDoList.addTask(newTask);
            taskInputField.clear();
            datePicker.setValue(null);
            ToDoListHandler.updateToDoList(user, toDoList);
            // update grid
             this.updateGrid();
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
            String taskName = currentTask.getTaskName();
            
            // check if date is empty
            Label dateLabel = new Label(""); 
            if (currentTask.getDateTime() != null) {
                LocalDate dateTime = currentTask.getDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                dateLabel.setText(dateTime.format(formatter));
            }
            Label taskLabel = new Label(taskName);
            
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
    
    @FXML
    public void OpenRegisterGroupWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterGroup.fxml"));
            Parent root = fxmlLoader.load();

            RegisterGroupController controller = fxmlLoader.getController();
            controller.setUser(this.user);
            // Create a new stage for the popup window
            Stage stage = new Stage();
            stage.setTitle("Register Group");
            stage.setScene(new Scene(root));

            // Show the new window
            stage.show();
    } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void OpenAddUserToGroupWindow() {
        try {
        // Load the FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddUserToGroup.fxml"));
        Parent root = fxmlLoader.load();

        // Initialize The addToUserGroup
        AddUserToGroupController controller = fxmlLoader.getController();
        controller.initializeAddToUserGroup(this.user);

        // Create a new stage for the popup window
        Stage stage = new Stage();
        stage.setTitle("Add user to group");
        stage.setScene(new Scene(root));

        // Show the new window
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
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

            AddNewTaskController addNewTaskController = fxmlLoader.getController();
            addNewTaskController.initializeTaskWindow(user, toDoList, this);

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

