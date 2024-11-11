package ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import api.ToDoListApiHandler;
import api.UserApiHandler;
import api.GroupApiHandler;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    @FXML
    private Pane groupOptionsPane;

    @FXML
    private Label groupOptionsLabel;

    @FXML
    private Button addButton;

    @FXML
    private Button openExpenseButton;

    @FXML
    private TableView<Task> tableView;

    @FXML
    private TableColumn<Task, String> taskNameColumn;

    @FXML
    private TableColumn<Task, String> dateColumn;

    @FXML
    private TableColumn<Task, String> descriptionColumn;

    @FXML
    private TableColumn<Task, String> priorityColumn;

    @FXML
    private TableColumn<Task, Boolean> checkBoxColumn;

    @FXML
    private Label currentlyViewingPath;
    
    private ToDoList toDoList;
    private User user;
    private String groupNameChat;
    private UserGroup groupInView;

    private ToDoListApiHandler toDoListApiHandler = new ToDoListApiHandler();
    private GroupApiHandler groupApiHandler = new GroupApiHandler();

    public void setUser(User user) {
        this.user = user;
        personal.setOnMouseClicked(event -> handleGroupClick(this.user.getUsername()));
        currentlyViewingPath.setText("Currently Viewing: " + user.getUsername() + " → Pending Tasks");
    }

    /**
     * Initializes the KollAppController by setting up the view and loading the user's to-do list.
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

        // Set the style for the "Add" button
        addButton.setOnMouseEntered(event -> handleAddButtonHover());
        addButton.setOnMouseExited(event -> handleAddButtonHoverExit());

        // Hide the group chat options by default
        groupChatButton.setVisible(false);
        groupOptionsPane.setVisible(false);
        groupOptionsLabel.setVisible(false);
        openExpenseButton.setVisible(false);
    }

    /**
     * Handles the hover effect for the "Add" button.
     */
    @FXML
    private void handleAddButtonHover() {
        addButton.setStyle("-fx-background-color: #19743F; -fx-background-radius: 10; -fx-cursor: hand;");
        animateButton(addButton, 1.05);
    }

    @FXML
    private void handleAddButtonHoverExit() {
        addButton.setStyle("-fx-background-color: #27AE60; -fx-background-radius: 10;");
        animateButton(addButton, 1.0);
    }

    /**
     * Animates the button size change.
     *
     * @param button the button to animate
     * @param scale  the target scale
     */
    private void animateButton(Button button, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setToX(scale);
        st.setToY(scale);
        st.play();
    }

    /**
     * Initializes the to-do list for the given user and updates the grid view with tasks.
     *
     * @param user The user whose to-do list is to be displayed
     */
    public void initializeToDoList(User user) {
        this.toDoList = toDoListApiHandler.loadToDoList(user);
        this.user = user;
        updateTableView();
    }

    /**
     * Populates the view with the user's group names, displaying them as clickable labels.
     * Ensures the user exists and handles potential missing data gracefully.
     */
    public void populateGroupView(List<String> groupNames) {
        vBoxContainer.getChildren().clear();
        if (groupNames != null) {
            groupNames.forEach(this::addGroupLabel);
            return;
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
        groupLabel.setStyle("-fx-cursor: hand; -fx-font-size: 15px; -fx-background-color: #7aadff; -fx-text-fill: white; -fx-padding: 10px; -fx-alignment: center; -fx-background-radius: 10; -fx-pref-width: 150px; -fx-pref-height: 40px;");
        groupLabel.setOnMouseEntered(event -> groupLabel.setStyle("-fx-cursor: hand; -fx-font-size: 15px; -fx-background-color:#6999e6; -fx-text-fill: white; -fx-padding: 10px; -fx-alignment: center; -fx-background-radius: 10; -fx-pref-width: 150px; -fx-pref-height: 40px;"));
        groupLabel.setOnMouseExited(event -> groupLabel.setStyle("-fx-cursor: hand; -fx-font-size: 15px; -fx-background-color:#7aadff; -fx-text-fill: white; -fx-padding: 10px; -fx-alignment: center; -fx-background-radius: 10; -fx-pref-width: 150px; -fx-pref-height: 40px;"));
        
        groupLabel.setPrefHeight(50);
        groupLabel.setPrefWidth(209);
        groupLabel.setAlignment(Pos.CENTER); // Center the text
        
        // Set up the click event
        groupLabel.setOnMouseClicked(event -> handleGroupClick(groupName));

        // Add the clickable label to the VBox
        vBoxContainer.getChildren().add(groupLabel);
        vBoxContainer.setPadding(new Insets(10, 0, 10, 0)); 
        vBoxContainer.setSpacing(10);
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
            groupOptionsPane.setVisible(false);
            groupOptionsLabel.setVisible(false);
            groupChatButton.setVisible(false);
            openExpenseButton.setVisible(false);
            currentlyViewingPath.setText("Currently Viewing: " + this.user.getUsername() + " → Pending Tasks"); // Short format for personal tasks
        } else if (groupNames.contains(groupName)) {
            this.groupNameChat = groupName;
            groupOptionsPane.setVisible(true);
            groupOptionsLabel.setVisible(true);
            groupChatButton.setVisible(true);
            openExpenseButton.setVisible(true);
            System.out.println("Perform action for " + groupName);
            changeCurrentTaskView(groupName);
            currentlyViewingPath.setText("Currently Viewing: " + this.user.getUsername() + " → " + groupName + " → Pending Tasks"); // Short format for group tasks
        }
        updateTableView();
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
        boolean isViewingCompletedTasks = completedLabel.getText().equals("Completed Tasks");

        // Toggle task view based on current label state
        if (isViewingCompletedTasks) {
            // Switch to Completed Tasks view
            // this.updateGridViewCompletedTasks();
            this.updateTableViewCompletedTasks();
            completedLabel.setText("Pending Tasks");
        } else {
            // Switch to Pending Tasks view
            this.updateTableView();
            completedLabel.setText("Completed Tasks");
        }

        // Update currentlyViewingPath based on group or personal view
        String viewType = isViewingCompletedTasks ? "Completed Tasks" : "Pending Tasks";
        if (groupInView == null) {
            // Personal task view
            currentlyViewingPath.setText("Currently Viewing: " + this.user.getUsername() + " → " + viewType);
        } else {
            // Group task view
            currentlyViewingPath.setText("Currently Viewing: " + this.user.getUsername() + " → " + groupInView.getGroupName() + " → " + viewType);
        }
    }

    @FXML
    public void updateTableView() {
        // Clear the current items in the TableView
        tableView.getItems().clear();
        if (toDoList == null) {
            toDoList = new ToDoList(); // Ensure toDoList is initialized
        }
        
        // Get the list of incomplete tasks and add them to the TableView
        List<Task> tasks = toDoList.getTasks();
        for (Task task : tasks) {
            if (!task.isCompleted()) { // Only add incomplete tasks
                tableView.getItems().add(task);
            }
        }
    
        // Set up the CheckBox column to mark tasks as completed when checked
        checkBoxColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox completeCheckBox = new CheckBox();
    
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
    
                if (empty) {
                    setGraphic(null);
                } else {
                    Task task = getTableView().getItems().get(getIndex());
    
                    // Set the CheckBox state to reflect the task's completion status
                    completeCheckBox.setSelected(task.isCompleted());
    
                    // Set action listener on the CheckBox
                    completeCheckBox.setOnAction(event -> {
                        if (completeCheckBox.isSelected()) {
                            // Mark the task as completed
                            task.setCompleted(true);
    
                            // Update the persistence layer
                            if (groupInView == null) {
                                toDoListApiHandler.updateToDoList(user, toDoList);
                            } else {
                                toDoListApiHandler.updateGroupToDoList(groupInView, toDoList);
                            }
    
                            // Remove the task from the table directly without calling updateTableView
                            tableView.getItems().remove(task);
                        }
                    });
    
                    // Display the CheckBox in the cell
                    setGraphic(completeCheckBox);
                }
            }
        });
    
        // Configure Task Name column
        taskNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskName()));
    
        // Configure Date column with centered alignment
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate dateTime = cellData.getValue().getDateTime();
            String date = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "";
            return new SimpleStringProperty(date);
        });
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Center-align text in the cell
                }
            }
        });
    
        // Configure Description column
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    
        // Configure Priority column with centered alignment and color coding
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority()));
        priorityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Clear any style if the cell is empty
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER); // Center-align text in the cell
                    
                    // Apply color based on priority level
                    switch (item) {
                        case "Low":
                            setStyle("-fx-text-fill: green;"); // Green for Low
                            break;
                        case "Medium":
                            setStyle("-fx-text-fill: orange;"); // Yellow for Medium
                            break;
                        case "High":
                            setStyle("-fx-text-fill: red;"); // Red for High
                            break;
                        default:
                            setStyle(""); // Clear style for unexpected values
                            break;
                    }
                }
            }
        });
    }

    @FXML
    public void logOut(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));

            stage.show();
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println("Error loading login screen: " + e.getMessage());
        }
    }

    @FXML
    public void updateTableViewCompletedTasks() {
        // Clear the current items in the TableView
        tableView.getItems().clear();
    
        // Get the list of completed tasks
        List<Task> tasks = toDoList.getTasks();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                tableView.getItems().add(task);
            }
        }
    
        // Set up the CheckBox column to remove tasks when checked
        checkBoxColumn.setCellFactory(column -> new TableCell<>() {
            private final CheckBox removeCheckBox = new CheckBox();
    
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeCheckBox);
                    Task task = getTableView().getItems().get(getIndex());
    
                    // Set CheckBox state and action
                    removeCheckBox.setSelected(true); // Checked by default
                    removeCheckBox.setOnAction(event -> {
                        if (!removeCheckBox.isSelected()) {
                            // Remove task from ToDoList
                            toDoList.removeTask(task);
    
                            // Update persistence layer
                            if (groupInView == null) {
                                toDoListApiHandler.updateToDoList(user, toDoList);
                            } else {
                                toDoListApiHandler.updateGroupToDoList(groupInView, toDoList);
                            }
    
                            // Refresh the TableView to reflect the task removal
                            updateTableViewCompletedTasks();
                        }
                    });
                }
            }
        });
    
        // Configure other columns to display task data
        taskNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskName()));
        dateColumn.setCellValueFactory(cellData -> {
            LocalDate dateTime = cellData.getValue().getDateTime();
            String date = dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "";
            return new SimpleStringProperty(date);
        });
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        priorityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPriority()));
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

    @FXML
    public void showExpense() {
        if (groupInView == null) {
            // No group selected, show error or disable the expense button
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ExpenseScreen.fxml"));
            Parent root = fxmlLoader.load();

            ExpenseController expenseController = fxmlLoader.getController();
            expenseController.initializeExpenseController(this.user, this.groupInView);

            Stage stage = new Stage();
            stage.setTitle("Expenses for " + groupInView.getGroupName());
            stage.setScene(new Scene(root));

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}