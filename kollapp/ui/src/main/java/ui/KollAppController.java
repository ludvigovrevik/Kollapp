package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import api.GroupApiHandler;
import api.UserApiHandler;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import persistence.GroupHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import api.ToDoListApiHandler;

/**
 * Controller class for the KollApp application.
 */
public class KollAppController {

    @FXML
    private GridPane taskGridView;

    @FXML
    private VBox vBoxContainer;

    @FXML
    private Label completedLabel;

    @FXML
    private Label personal;

    @FXML
    private Button groupChatButton;
    
    private ToDoList toDoList;
    private User user;
    private String groupNameChat;
    private UserGroup groupInView;

    private final ToDoListApiHandler toDoListApiHandler = new ToDoListApiHandler();
    private final GroupApiHandler groupApiHandler = new GroupApiHandler();
    private final UserApiHandler userApiHandler = new UserApiHandler();

    public void setUser(User user) {
        this.user = user;
        personal.setOnMouseClicked(event -> handleGroupClick(this.user.getUsername()));
    }

     /**
     * Initializes the controller, setting event handlers for labels 
     * and preparing the interface for interaction.
     */
    public void initialize() {
        // Set the label to act like a button
        completedLabel.setOnMouseClicked(this::handleLabelClick);
        personal.setOnMouseClicked(event -> handleGroupClick(this.user.getUsername()));

        // Change the cursor to hand when hovering over the label
        completedLabel.setStyle("-fx-cursor: hand;");
        personal.setStyle("-fx-cursor: hand;");
        VBox.setVgrow(vBoxContainer, Priority.ALWAYS);
        groupInView = null;

        groupChatButton.setVisible(false);
    }

    /**
     * Initializes the to-do list for the given user and updates the grid view with tasks.
     *
     * @param user The user whose to-do list is to be displayed
     */
    public void initializeToDoList(User user) {
        this.toDoList = toDoListApiHandler.loadToDoList(user);
        this.user = user;
        updateGrid();
    }

    /**
     * Populates the view with the user's group names, displaying them as clickable labels.
     * Ensures the user exists and handles potential missing data gracefully.
     */
    public void populateGroupView() {
        vBoxContainer.getChildren().clear();

        Optional<User> optionalUser = userApiHandler.getUser(this.user.getUsername());
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            
            this.user = currentUser;

            List<String> groupNames = currentUser.getUserGroups();
            groupNames.forEach(this::addGroupLabel);
        } else {
            System.err.println("User not found in populateGroupView: " + this.user.getUsername());
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
        groupLabel.setOnMouseClicked(event -> handleGroupClick(groupName));

        // Add the clickable label to the VBox
        vBoxContainer.getChildren().add(groupLabel);
    }

    /**
     * Handles the click event for the dynamic group labels.
     * 
     * @param groupName The name of the group clicked
     */
    private void handleGroupClick(String groupName) {
        List<String> groupNames = this.user.getUserGroups();
        System.out.println("Clicked on group: " + groupName);

        if (groupName.equals(this.user.getUsername())) {
            changeCurrentTaskView(this.user.getUsername());
            groupChatButton.setVisible(false);
        } else if (groupNames.contains(groupName)) {
            this.groupNameChat = groupName;
            groupChatButton.setVisible(true);
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
        } else if (completedLabel.getText().equals("Tasks")) {
            this.updateGrid();
            completedLabel.setText("Completed");
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

        for (Task task : tasks) {
            if (task.isCompleted()) {
                continue; // Skip completed tasks
            }

            String taskName = task.getTaskName();
            String taskDescription = task.getDescription();
            String priority = task.getPriority();

            Label dateLabel = new Label("");
            if (task.getDateTime() != null) {
                LocalDate dateTime = task.getDateTime();
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
                    task.setCompleted(true);
                    if (groupInView == null) {
                        toDoListApiHandler.updateToDoList(user, toDoList);
                    } else {
                        toDoListApiHandler.updateGroupToDoList(groupInView, toDoList);
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

    @FXML
    public void logOut() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));

            stage.show();
            Stage currentStage = (Stage) taskGridView.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
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
                CheckBox checkBox = getCheckBox(currentTask);

                taskGridView.add(checkBox, 0, row);
                taskGridView.add(taskLabel, 1, row);
                taskGridView.add(dateLabel, 2, row);
                row++;
            }
        }
    }

    private CheckBox getCheckBox(Task currentTask) {
        CheckBox checkBox = new CheckBox();

        // Add event listener to the CheckBox for task removal
        checkBox.setOnAction(event -> {
            toDoList.removeTask(currentTask);
            if (groupInView == null) {
                toDoListApiHandler.updateToDoList(user, toDoList);
            } else {
                toDoListApiHandler.updateGroupToDoList(groupInView, toDoList);
            }
            updateGrid(); // Refresh the grid
        });
        return checkBox;
    }

    /**
     * Changes the current task view to the user's personal tasks or a group's tasks.
     *
     * @param taskOwner The name of the user or group whose tasks to display
     */
    public void changeCurrentTaskView(String taskOwner) {
        if (taskOwner.equals(this.user.getUsername())) {
            groupInView = null;
            this.toDoList = toDoListApiHandler.loadToDoList(this.user);
        } else {
            Optional<UserGroup> groupOptional = groupApiHandler.getGroup(taskOwner);

            if (groupOptional.isPresent()) {
                UserGroup group = groupOptional.get();
                this.groupNameChat = group.getGroupName();
                groupInView = group;
                try {
                    this.toDoList = toDoListApiHandler.loadGroupToDoList(group);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error loading group tasks: " + e.getMessage());
                }
            } else {
                System.out.println("Group not found: " + taskOwner);
            }
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
            controller.initialize(this.user, this);
            Stage stage = new Stage();
            stage.setTitle("Register Group");
            stage.setScene(new Scene(root));

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
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

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void showGroupChat() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GroupChatScreen.fxml"));
            Parent root = fxmlLoader.load();

            GroupChatController groupChatController = fxmlLoader.getController();

            // Pass the groupName to the initializeGroupChatWindow method
            groupChatController.initializeGroupChatWindow(this.user, this.groupNameChat);

            Stage stage = new Stage();
            stage.setTitle(this.groupNameChat + " discussion");
            stage.setScene(new Scene(root));

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}