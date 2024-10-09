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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import persistence.GroupHandler;
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
    private UserGroup groupInView;

    private ToDoListHandler toDoListHandler = new ToDoListHandler();
    private GroupHandler groupHandler = new GroupHandler();

    @FXML
    private VBox vBoxContainer;

    @FXML
    private Label completedLabel;

    @FXML 
    private Label personal;

    @FXML
    public void initialize() {
        // Set the label to act like a button
        completedLabel.setOnMouseClicked(this::handleLabelClick);
        personal.setOnMouseClicked(event -> handleGroupClick(event, this.user.getUsername()));

        // Change the cursor to hand when hovering over the label
        completedLabel.setStyle("-fx-cursor: hand;");
        personal.setStyle("-fx-cursor: hand;");
        VBox.setVgrow(vBoxContainer, Priority.ALWAYS);
        groupInView = null;

    }
    
    public void populateGroupView() {
        vBoxContainer.getChildren().clear();
        List<String> groupNames = this.user.getUserGroups();
        for (String groupName : groupNames) {
            addGroupLabel(groupName);
        }
    }

    private void addGroupLabel(String groupName) {
        Label groupLabel = new Label(groupName);
        
        // Set style to make the label look like a button
        groupLabel.setStyle("-fx-cursor: hand; -fx-background-color: #7aadff; -fx-text-fill: white; -fx-padding: 10px; -fx-alignment: center;");
        groupLabel.setPrefHeight(50);
        groupLabel.setPrefWidth(200);
        groupLabel.setAlignment(Pos.CENTER);  // Center the text
        
        // Set up the click event
        groupLabel.setOnMouseClicked(event -> handleGroupClick(event, groupName));

        // Add the clickable label to the VBox
        vBoxContainer.getChildren().add(groupLabel);
    }
    /**
 * Handles the click event for the dynamic group labels.
 * @param event The mouse click event
 * @param groupName The name of the group clicked
 */
    private void handleGroupClick(MouseEvent event, String groupName) {
        List<String> groupNames = this.user.getUserGroups();
        System.out.println("Clicked on group: " + groupName);
        
        // You can add logic here to perform an action based on the group clicked.
        // For example, switch scenes or load group-specific data.
        if (groupName.equals(this.user.getUsername())) {
            changeCurrentTaskView(this.user.getUsername());
        } else if (groupNames.contains(groupName)) {
            System.out.println("Perform action for " + groupName);
            changeCurrentTaskView(groupName);
        } 
        updateGrid();
    }

    public UserGroup getGroupInView() {
        return groupInView;
    }
    
    // Handle the click event on the label Completed
    @FXML
    private void handleLabelClick(MouseEvent event) {
        if (completedLabel.getText().equals("Completed")) {
            this.updateGridViewCompletedTasks();
            completedLabel.setText("Tasks");
            return;
        } else if (completedLabel.getText().equals("Tasks")) {
            this.updateGrid();
            completedLabel.setText("Completed");
            return;
        }
    }

    @FXML
    public void updateGrid() {
        // Clear grid view before retrieving tasks
        taskGridView.getChildren().clear();
        List<Task> tasks = toDoList.getTasks();
        
        // Use a separate row counter
        int row = 0;
        
        // Iterate through all tasks
        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);
            if (currentTask.isCompleted()) {
                continue; // Skip completed tasks
            }

            String taskName = currentTask.getTaskName();
            String taskDescription = currentTask.getDescription();
            String priority = currentTask.getPriority();
            
            // check if date is empty
            Label dateLabel = new Label(""); 
            if (currentTask.getDateTime() != null) {
                LocalDate dateTime = currentTask.getDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                dateLabel.setText(dateTime.format(formatter));
            }

            CheckBox checkBox = new CheckBox();
            Label taskLabel = new Label(taskName);
            Label taskDescriptionLabel = new Label(taskDescription);
            Label priorityLabel = new Label(priority);
            
            // Add event listener to the CheckBox
            checkBox.setOnAction(event -> {
                if (checkBox.isSelected()) {
                    currentTask.setCompleted(true); // Set the task as completed when checkbox is selected
                    if (groupInView == null) {
                        toDoListHandler.updateToDoList(user, toDoList);
                    } else {
                        toDoListHandler.updateGroupToDoList(groupInView, toDoList);
                    }
                    updateGrid();  // Refresh the grid
                }
            });

            // Add elements to the grid using the row counter
            taskGridView.add(checkBox, 0, row);
            taskGridView.add(taskLabel, 1, row);
            taskGridView.add(dateLabel, 2, row);
            taskGridView.add(taskDescriptionLabel, 3, row);
            taskGridView.add(priorityLabel, 4, row);
            GridPane.setVgrow(taskLabel, Priority.ALWAYS);
            // Increment row counter for the next task
            row++; 
        }
    }

    @FXML
    public void updateGridViewCompletedTasks() {
        // Clear grid view before retrieving tasks
        if (taskGridView.getChildren().size() > 0 || taskGridView.getChildren() != null) {
            taskGridView.getChildren().clear();
        }

        List<Task> tasks = toDoList.getTasks();
        int row = 0;
        // Iterate through all tasks
        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);
            String taskDescription = currentTask.getTaskName();
            
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
                    if (groupInView == null) {
                        toDoListHandler.updateToDoList(user, toDoList);
                    } else {
                        toDoListHandler.updateGroupToDoList(groupInView, toDoList);
                    }
                    updateGrid();  // Refresh the grid
                }
            });

            // Only tasks that are completed are shown in the completed tasks view
            if (currentTask.isCompleted()) {
                // Add elements to the grid
                taskGridView.add(checkBox, 0, row);
                taskGridView.add(taskLabel, 1, row);
                taskGridView.add(dateLabel, 2, row);
            }
            row++; 
        }
    }

    public void changeCurrentTaskView(String taskOwner) {
        if (taskOwner.equals(this.user.getUsername())) {
            groupInView = null;
            this.toDoList = toDoListHandler.loadToDoList(this.user);
            return;
        }
        // find the todolist of the group you switch to
        UserGroup group = groupHandler.getGroup(taskOwner);
        this.toDoList = toDoListHandler.loadGroupToDoList(group);
        groupInView = group;
    }
    
    @FXML
    public void openRegisterGroupWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterGroup.fxml"));
            Parent root = fxmlLoader.load();

            RegisterGroupController controller = fxmlLoader.getController();
            controller.initialize(user, this);
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
    public void openAddUserToGroupWindow() {
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
        ToDoListHandler handler = new ToDoListHandler();
        this.toDoList = handler.loadToDoList(user);
        this.user = user;
        updateGrid();
    }

    @FXML
    public void showDialog() {
        try {
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

