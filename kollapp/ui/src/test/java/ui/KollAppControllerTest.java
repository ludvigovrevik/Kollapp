package ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import api.GroupApiHandler;
import api.ToDoListApiHandler;
import core.Task;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Unit tests for the KollAppController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class KollAppControllerTest {

     // Headless mode is enabled
    static private boolean headless = true;

    /**
     * Sets up the environment for headless mode if the 'headless' flag is true.
     * This method configures various system properties required for running
     * JavaFX tests in a headless environment.
     * 
     * Properties set:
     * - testfx.headless: Enables TestFX headless mode.
     */
    @BeforeAll
    static void setupHeadlessMode() {
        if(headless){
            System.setProperty("testfx.headless", "true");

            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    @AfterEach
    public void tearDown(FxRobot robot) {
        // Close all windows except the primary stage
        Platform.runLater(() -> {
            for (Window window : robot.robotContext().getWindowFinder().listWindows()) {
                if (window instanceof Stage && ((Stage) window).isShowing() && window != robot.robotContext().getWindowFinder().window(0)) {
                    ((Stage) window).close();
                }
            }
        });
        // Wait for windows to close
        robot.sleep(500);
    }

    /**
     * Initializes the test environment by loading the Kollektiv.fxml and setting up the controller.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    private void start(Stage stage) throws Exception {
        // Mock the ToDoListApiHandler
        ToDoListApiHandler mockApiHandler = mock(ToDoListApiHandler.class);
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Test Task", LocalDate.now(), "Test Description", "High"));
        when(mockApiHandler.loadToDoList(any(User.class))).thenReturn(toDoList);

        // Mock the GroupApiHandler
        GroupApiHandler mockGroupApiHandler = mock(GroupApiHandler.class);
        UserGroup testGroup = new UserGroup("TestGroup");
        when(mockGroupApiHandler.getGroup("TestGroup")).thenReturn(Optional.of(testGroup));
        when(mockApiHandler.loadGroupToDoList(testGroup)).thenReturn(new ToDoList());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Kollektiv.fxml"));
        Parent root = loader.load();

        KollAppController controller = loader.getController();

        // Set the mocked API handlers
        Field apiHandlerField = KollAppController.class.getDeclaredField("toDoListApiHandler");
        apiHandlerField.setAccessible(true);
        apiHandlerField.set(controller, mockApiHandler);

        Field groupApiHandlerField = KollAppController.class.getDeclaredField("groupApiHandler");
        groupApiHandlerField.setAccessible(true);
        groupApiHandlerField.set(controller, mockGroupApiHandler);

        User user = new User("KollAppControllerUserTest", "passwordd");
        user.addUserGroup("TestGroup");
        controller.setUser(user);
        controller.initializeToDoList(user);
        controller.populateGroupView(user.getUserGroups());

        stage.setScene(new Scene(root));
        stage.show();
    }


    /**
     * Verifies that the "+" button is present and has the correct text.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test presence of '+' button in KollAppController UI")
    @Tag("ui")
    public void shouldContainAddButton(FxRobot robot) {
        Button addButton = robot.lookup("#addButton").queryAs(Button.class);
        Assertions.assertThat(addButton).isNotNull();
        Assertions.assertThat(addButton).hasText("+ Add task");
    }

    @Test
    @DisplayName("Test logout functionality")
    @Tag("ui")
    public void shouldOpenLoginScreenOnLogout(FxRobot robot) {
        // Click the logout button
        robot.clickOn("#logoutButton");
        
        // Verify that a new window with title "Login" appears
        robot.lookup(".root").queryAs(Parent.class);
        Assertions.assertThat(robot.window("Login")).isShowing();
    }

    @Test
    @DisplayName("Test table view columns are present with correct headers")
    @Tag("ui")
    public void shouldHaveCorrectTableColumns(FxRobot robot) {
        // Verify each column exists and has the correct header text
        @SuppressWarnings("unchecked")
        TableView<Task> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        
        Assertions.assertThat(tableView).isNotNull();
        
        // Get all columns with proper generic type
        ObservableList<TableColumn<Task, ?>> columns = tableView.getColumns();
        
        // Verify the number of columns
        Assertions.assertThat(columns).hasSize(5);
        
        // Verify column headers
        Assertions.assertThat(columns.get(4).getText()).isEqualTo("✓");
        Assertions.assertThat(columns.get(0).getText()).isEqualTo("Task");
        Assertions.assertThat(columns.get(1).getText()).isEqualTo("Date");
        Assertions.assertThat(columns.get(2).getText()).isEqualTo("Description");
        Assertions.assertThat(columns.get(3).getText()).isEqualTo("Priority");
    }

    @Test
    @DisplayName("Test completed tasks label toggle")
    @Tag("ui")
    public void shouldToggleCompletedTasksLabel(FxRobot robot) {
        // Get the label
        Label completedLabel = robot.lookup("#completedLabel").queryAs(Label.class);
        
        // Verify initial state
        Assertions.assertThat(completedLabel).hasText("Completed Tasks");
        
        // Click the label
        robot.clickOn("#completedLabel");
        
        // Verify text changed
        Assertions.assertThat(completedLabel).hasText("Pending Tasks");
        
        // Click again to toggle back
        robot.clickOn("#completedLabel");
        
        // Verify text returned to original
        Assertions.assertThat(completedLabel).hasText("Completed Tasks");
    }

    @Test
    @DisplayName("Test group management buttons presence and state")
    @Tag("ui")
    public void shouldHaveGroupManagementButtons(FxRobot robot) {
        // Get the buttons
        Button createGroupButton = robot.lookup("#createGroupButton").queryAs(Button.class);
        Button addUserToGroupButton = robot.lookup("#addUserToGroupButton").queryAs(Button.class);
        
        // Verify buttons exist
        Assertions.assertThat(createGroupButton).isNotNull();
        Assertions.assertThat(addUserToGroupButton).isNotNull();
        
        // Verify button text
        Assertions.assertThat(createGroupButton).hasText("New group");
        Assertions.assertThat(addUserToGroupButton).hasText("Add member");
        
        // Verify buttons are enabled
        Assertions.assertThat(createGroupButton.isDisabled()).isFalse();
        Assertions.assertThat(addUserToGroupButton.isDisabled()).isFalse();
    }

    @Test
    @DisplayName("Test My tasks label functionality")
    @Tag("ui")
    public void shouldHandleMyTasksLabelCorrectly(FxRobot robot) {
        // Get the personal tasks label and current view path label
        Label personalLabel = robot.lookup("#personal").queryAs(Label.class);
        Label currentlyViewingPath = robot.lookup("#currentlyViewingPath").queryAs(Label.class);
        
        // Verify personal label exists and has correct text
        Assertions.assertThat(personalLabel).isNotNull();
        Assertions.assertThat(personalLabel).hasText("My tasks");
        
        // Click the personal label
        robot.clickOn("#personal");
        
        // Verify the current view path updates correctly
        Assertions.assertThat(currentlyViewingPath)
            .hasText("Currently Viewing: KollAppControllerUserTest → Pending Tasks");
    }

    @Test
    @DisplayName("Test group options pane visibility management")
    @Tag("ui")
    public void shouldManageGroupOptionsPaneVisibility(FxRobot robot) {
        // Get the group options components
        Pane groupOptionsPane = robot.lookup("#groupOptionsPane").queryAs(Pane.class);
        Button groupChatButton = robot.lookup("#groupChatButton").queryAs(Button.class);
        Button openExpenseButton = robot.lookup("#openExpenseButton").queryAs(Button.class);
        Label groupOptionsLabel = robot.lookup("#groupOptionsLabel").queryAs(Label.class);
        
        // Verify initial state - should be hidden
        Assertions.assertThat(groupOptionsPane.isVisible()).isFalse();
        Assertions.assertThat(groupChatButton.isVisible()).isFalse();
        Assertions.assertThat(openExpenseButton.isVisible()).isFalse();
        Assertions.assertThat(groupOptionsLabel.isVisible()).isFalse();
        
        // Verify button text when they exist (even if not visible)
        Assertions.assertThat(groupChatButton).hasText("Open chat");
        Assertions.assertThat(openExpenseButton).hasText("Show expenses");
        Assertions.assertThat(groupOptionsLabel).hasText("Group Options");
    }

    @Test
    @DisplayName("Test current viewing path updates when toggling task view")
    @Tag("ui")
    public void shouldUpdatePathWhenTogglingTaskView(FxRobot robot) {
        // Get the labels
        Label currentlyViewingPath = robot.lookup("#currentlyViewingPath").queryAs(Label.class);
        Label completedLabel = robot.lookup("#completedLabel").queryAs(Label.class);
        
        // Verify initial state
        String username = "KollAppControllerUserTest";
        String initialPath = "Currently Viewing: " + username + " → Pending Tasks";
        Assertions.assertThat(currentlyViewingPath).hasText(initialPath);
        
        // Click completed tasks label to switch view
        robot.clickOn("#completedLabel");
        
        // Verify path updates for completed tasks view
        String completedPath = "Currently Viewing: " + username + " → Completed Tasks";
        Assertions.assertThat(currentlyViewingPath).hasText(completedPath);
        
        // Click again to switch back to pending tasks
        robot.clickOn("#completedLabel");
        
        // Verify path returns to original state
        Assertions.assertThat(currentlyViewingPath).hasText(initialPath);
    }

    @Test
    @DisplayName("Test that clicking Add Task button opens Add New Task dialog")
    public void shouldOpenAddNewTaskDialog(FxRobot robot) {
        // Click the addButton
        robot.clickOn("#addButton");

        // Verify that the Add New Task window appears
        Assertions.assertThat(robot.window("Add New Task")).isShowing();
    }

    @Test
    @DisplayName("Test that clicking New Group button opens Register Group window")
    public void shouldOpenRegisterGroupWindow(FxRobot robot) {
        // Click the createGroupButton
        robot.clickOn("#createGroupButton");

        // Verify that the Register Group window appears
        Assertions.assertThat(robot.window("Register Group")).isShowing();
    }

    @Test
    @DisplayName("Test that clicking Add Member button opens Add User to Group window")
    public void shouldOpenAddUserToGroupWindow(FxRobot robot) {
        // Click the addUserToGroupButton
        robot.clickOn("#addUserToGroupButton");

        // Verify that the Add User to Group window appears
        Assertions.assertThat(robot.window("Add user to group")).isShowing();
    }

    @Test
    @DisplayName("Test hover effect on Add Task button")
    public void shouldChangeScaleOnHoverAddButton(FxRobot robot) {
        Button addButton = robot.lookup("#addButton").queryAs(Button.class);

        // Get the initial scale
        double initialScaleX = addButton.getScaleX();
        double initialScaleY = addButton.getScaleY();

        // Move mouse over the addButton
        robot.moveTo(addButton);

        // Wait for the hover effect
        robot.sleep(1000);

        // Verify that the scale has increased
        Assertions.assertThat(addButton.getScaleX()).isGreaterThan(initialScaleX);
        Assertions.assertThat(addButton.getScaleY()).isGreaterThan(initialScaleY);
    }

    @Test
    @DisplayName("Test clicking on a group label updates view and shows group options")
    public void shouldHandleGroupLabelClick(FxRobot robot) {
        // Click on the group label
        robot.clickOn("TestGroup");

        // Verify the currentlyViewingPath updates
        Label currentlyViewingPath = robot.lookup("#currentlyViewingPath").queryAs(Label.class);
        Assertions.assertThat(currentlyViewingPath).hasText("Currently Viewing: KollAppControllerUserTest → TestGroup → Pending Tasks");

        // Verify group options pane becomes visible
        Pane groupOptionsPane = robot.lookup("#groupOptionsPane").queryAs(Pane.class);
        Assertions.assertThat(groupOptionsPane.isVisible()).isTrue();
    }

    @Test
    @DisplayName("Test clicking on My tasks hides group options pane")
    public void shouldHideGroupOptionsPaneWhenClickingPersonalLabel(FxRobot robot) {
        // Click on the group label first
        robot.clickOn("TestGroup");

        // Ensure the pane is visible
        Pane groupOptionsPane = robot.lookup("#groupOptionsPane").queryAs(Pane.class);
        Assertions.assertThat(groupOptionsPane.isVisible()).isTrue();

        // Click on the personal label
        robot.clickOn("#personal");

        // Verify the pane is hidden
        Assertions.assertThat(groupOptionsPane.isVisible()).isFalse();
    }

    @Test
    @DisplayName("Test that clicking Open chat button opens Group Chat window")
    public void shouldOpenGroupChatWindow(FxRobot robot) {
        // Click on the group label
        robot.clickOn("TestGroup");

        // Click on the groupChatButton
        robot.clickOn("#groupChatButton");

        // Verify the Group Chat window appears
        Assertions.assertThat(robot.window("TestGroup discussion")).isShowing();
    }

    @Test
    @DisplayName("Test that clicking Show expenses button opens Expense window")
    public void shouldOpenExpenseWindow(FxRobot robot) {
        // Click on the group label
        robot.clickOn("TestGroup");

        // Click on the openExpenseButton
        robot.clickOn("#openExpenseButton");

        // Verify the Expense window appears
        Assertions.assertThat(robot.window("Expenses for TestGroup")).isShowing();

        // Close the Expense window
        Platform.runLater(() -> {
            Stage expenseStage = (Stage) robot.window("Expenses for TestGroup");
            expenseStage.close();
        });
        robot.sleep(500);
    }


    @Test
    @DisplayName("Test removing a completed task from Completed Tasks view")
    public void shouldRemoveTaskWhenMarkedAsCompleted(FxRobot robot) {
        // Ensure the task is present in the pending tasks
        TableView<Task> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        Assertions.assertThat(tableView.getItems()).hasSize(1);

        // Mark the task as completed
        robot.clickOn(lookup -> lookup.getStyleClass().contains("check-box"));

        // Wait for the action to complete
        robot.sleep(500);

        // Switch to Completed Tasks view
        robot.clickOn("#completedLabel");

        // Wait for the table to update
        robot.sleep(500);

        // Ensure the task is present in completed tasks
        tableView = robot.lookup("#tableView").queryAs(TableView.class);
        Assertions.assertThat(tableView.getItems()).hasSize(1);

        // Click on the checkbox to remove the task
        robot.clickOn(lookup -> lookup.getStyleClass().contains("check-box"));

        // Wait for the action
        robot.sleep(500);

        // Verify the task is removed
        Assertions.assertThat(tableView.getItems()).hasSize(0);
    }
    
}
