package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    @FXML
    private VBox vBoxContainer;

    @FXML
    private Label completedLabel;

    @FXML
    private Label personal;
    
    private ToDoList toDoList;
    private User user;
    private UserGroup groupInView;

    private ToDoListHandler toDoListHandler = new ToDoListHandler();
    private GroupHandler groupHandler = new GroupHandler();

    public void setToDoListHandler(ToDoListHandler toDoListHandler) {
        this.toDoListHandler = toDoListHandler;
    }

    public void setGroupHandler(GroupHandler groupHandler) {
        this.groupHandler = groupHandler;
    }

    public GridPane getTaskGridView() {
        return taskGridView;
    }

    public VBox getVBoxContainer() {
        return vBoxContainer;
    }

    public Label getCompletedLabel() {
        return completedLabel;
    }

    public void setUser(User user) {
        this.user = user;
        personal.setOnMouseClicked(event -> handleGroupClick(event, this.user.getUsername()));
    }

     /**
     * Initializes the controller, setting event handlers for labels 
     * and preparing the interface for interaction.
     */
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

    /**
     * Initializes the to-do list for the given user and updates the grid view with tasks.
     *
     * @param user The user whose to-do list is to be displayed
     */
    public void innitializeToDoList(User user) {
        this.toDoList = toDoListHandler.loadToDoList(user);
        this.user = user;
        updateGrid();
    }

    /**
     * Populates the view with the user's group names, displaying them as clickable labels.
     */
    public void populateGroupView() {
        vBoxContainer.getChildren().clear();
        List<String> groupNames = this.user.getUserGroups();
        for (String groupName : groupNames) {
            addGroupLabel(groupName);
        }
    }

    /**
     * Adds a label for a group, making it clickable and styled like a button.
     *
     * @param groupName the name of the group to add to the view
     */
    private void addGroupLabel(String groupName) {
        Label groupLabel = new Label(groupName);

        // Set style to make the label look like a button
        groupLabel.setStyle("-fx-cursor: hand; -fx-background-color: #7aadff; -fx-text-fill: white; -fx-padding: 10px; -fx-alignment: center;");
        groupLabel.setPrefHeight(50);
        groupLabel.setPrefWidth(200);
        groupLabel.setAlignment(Pos.CENTER); // Center the text

        // Set up the click event
        groupLabel.setOnMouseClicked(event -> handleGroupClick(event, groupName));

        // Add the clickable label to the VBox
        vBoxContainer.getChildren().add(groupLabel);
    }

    /**
     * Handles the click event for the dynamic group labels.
     * 
     * @param event     The mouse click event
     * @param groupName The name of the group clicked
     */
    private void handleGroupClick(MouseEvent event, String groupName) {
        List<String> groupNames = this.user.getUserGroups();
        System.out.println("Clicked on group: " + groupName);

        if (groupName.equals(this.user.getUsername())) {
            changeCurrentTaskView(this.user.getUsername());
        } else if (groupNames.contains(groupName)) {
            System.out.println("Perform action for " + groupName);
            changeCurrentTaskView(groupName);
        }
        updateGrid();
    }

    /**
     * Gets the group currently being viewed in the application.
     *
     * @return the UserGroup currently being viewed
     */
    public UserGroup getGroupInView() {
        return groupInView;
    }

    /**
     * Handles the click event for toggling between completed and pending tasks.
     *
     * @param event The mouse event triggered by clicking the "Completed" label
     */
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
    
    /**
     * Updates the task grid view by clearing existing tasks and populating it with
     * tasks from the to-do list that are not completed. Each task is displayed with
     * its name, description, priority, and date (if available). A checkbox is added
     * to each task, allowing the user to mark it as completed. When a task is marked
     * as completed, the to-do list is updated accordingly, and the grid is refreshed.
     */
    @FXML
    public void updateGrid() {
        taskGridView.getChildren().clear();
        List<Task> tasks = toDoList.getTasks();

        int row = 0;

        for (int i = 0; i < tasks.size(); i++) {
            Task currentTask = tasks.get(i);
            if (currentTask.isCompleted()) {
                continue; // Skip completed tasks
            }

            String taskName = currentTask.getTaskName();
            String taskDescription = currentTask.getDescription();
            String priority = currentTask.getPriority();

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
                    currentTask.setCompleted(true);
                    if (groupInView == null) {
                        toDoListHandler.updateToDoList(user, toDoList);
                    } else {
                        toDoListHandler.updateGroupToDoList(groupInView, toDoList);
                    }
                    updateGrid();
                }
            });

            taskGridView.add(checkBox, 0, row);
            taskGridView.add(taskLabel, 1, row);
            taskGridView.add(dateLabel, 2, row);
            taskGridView.add(taskDescriptionLabel, 3, row);
            taskGridView.add(priorityLabel, 4, row);
            GridPane.setVgrow(taskLabel, Priority.ALWAYS);
            row++;
        }
    }

    /**
     * Updates the grid view to display only the completed tasks.
     * This method clears the current grid view and repopulates it with tasks
     * that are marked as completed. Each task is displayed with a checkbox,
     * task description, and date (if available). The checkbox allows for the
     * removal of the task from the to-do list.
     */
    @FXML
    public void updateGridViewCompletedTasks() {
        taskGridView.getChildren().clear();
        List<Task> tasks = toDoList.getTasks();
        int row = 0;

        for (Task currentTask : tasks) {
            if (currentTask.isCompleted()) {
                String taskDescription = currentTask.getTaskName();

                Label dateLabel = new Label("");
                if (currentTask.getDateTime() != null) {
                    LocalDate dateTime = currentTask.getDateTime();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                    dateLabel.setText(dateTime.format(formatter));
                }

                Label taskLabel = new Label(taskDescription);
                CheckBox checkBox = new CheckBox();

                // Add event listener to the CheckBox for task removal
                checkBox.setOnAction(event -> {
                    toDoList.removeTask(currentTask);
                    if (groupInView == null) {
                        toDoListHandler.updateToDoList(user, toDoList);
                    } else {
                        toDoListHandler.updateGroupToDoList(groupInView, toDoList);
                    }
                    updateGrid(); // Refresh the grid
                });

                taskGridView.add(checkBox, 0, row);
                taskGridView.add(taskLabel, 1, row);
                taskGridView.add(dateLabel, 2, row);
                row++;
            }
        }
    }

    /**
     * Changes the current task view to the user's personal tasks or a group's tasks.
     *
     * @param taskOwner The name of the user or group whose tasks to display
     */
    public void changeCurrentTaskView(String taskOwner) {
        if (taskOwner.equals(this.user.getUsername())) {
            groupInView = null;
            this.toDoList = toDoListHandler.loadToDoList(this.user);
        } else {
            UserGroup group = groupHandler.getGroup(taskOwner);
            this.toDoList = toDoListHandler.loadGroupToDoList(group);
            groupInView = group;
        }
    }

    /**
     * Opens the "Register Group" window, allowing the user to create a new group.
     */
    @FXML
    public void openRegisterGroupWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RegisterGroup.fxml"));
            Parent root = fxmlLoader.load();

            RegisterGroupController controller = fxmlLoader.getController();
            controller.initialize(user, this);
            Stage stage = new Stage();
            stage.setTitle("Register Group");
            stage.setScene(new Scene(root));

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the "Add User to Group" window, allowing the user to add other users to a group.
     */
    @FXML
    public void openAddUserToGroupWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddUserToGroup.fxml"));
            Parent root = fxmlLoader.load();

            AddUserToGroupController controller = fxmlLoader.getController();
            controller.initializeAddToUserGroup(this.user);

            Stage stage = new Stage();
            stage.setTitle("Add user to group");
            stage.setScene(new Scene(root));

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the "Add New Task" dialog, allowing the user to create a new task.
     */
    @FXML
    public void showDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddNewTask.fxml"));
            Parent root = fxmlLoader.load();

            AddNewTaskController addNewTaskController = fxmlLoader.getController();
            addNewTaskController.initializeTaskWindow(user, toDoList, this);

            Stage stage = new Stage();
            stage.setTitle("Add New Task");
            stage.setScene(new Scene(root));

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}