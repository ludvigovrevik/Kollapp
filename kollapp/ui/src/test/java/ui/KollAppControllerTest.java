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

import client.GroupApiHandler;
import client.ToDoListApiHandler;
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
 * Unit tests for the {@link KollAppController} class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class KollAppControllerTest {

    private static boolean headless = true;

    /**
     * Enables headless mode if required, allowing TestFX tests to run without a display.
     */
    @BeforeAll
    private static void setupHeadlessMode() {
        if (headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    /**
     * Closes additional windows after each test to ensure a clean test environment.
     */
    @AfterEach
    private void tearDown(FxRobot robot) {
        Platform.runLater(() -> {
            for (Window window : robot.robotContext().getWindowFinder().listWindows()) {
                if (window instanceof Stage && ((Stage) window).isShowing() && window != robot.robotContext().getWindowFinder().window(0)) {
                    ((Stage) window).close();
                }
            }
        });
        robot.sleep(500);
    }

    /**
     * Initializes the KollAppController with test data and dependencies.
     *
     * @param stage the primary stage for JavaFX tests
     */
    @Start
    private void start(Stage stage) throws Exception {
        ToDoListApiHandler mockApiHandler = mock(ToDoListApiHandler.class);
        ToDoList toDoList = new ToDoList();
        toDoList.addTask(new Task("Test Task", LocalDate.now(), "Test Description", "High"));
        when(mockApiHandler.loadToDoList(any(User.class))).thenReturn(Optional.of(toDoList));

        GroupApiHandler mockGroupApiHandler = mock(GroupApiHandler.class);
        UserGroup testGroup = new UserGroup("TestGroup");
        when(mockGroupApiHandler.getGroup("TestGroup")).thenReturn(Optional.of(testGroup));
        when(mockApiHandler.loadGroupToDoList(testGroup)).thenReturn(Optional.of(new ToDoList()));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Kollektiv.fxml"));
        Parent root = loader.load();

        KollAppController controller = loader.getController();

        Field apiHandlerField = KollAppController.class.getDeclaredField("toDoListApiHandler");
        apiHandlerField.setAccessible(true);
        apiHandlerField.set(controller, mockApiHandler);

        Field groupApiHandlerField = KollAppController.class.getDeclaredField("groupApiHandler");
        groupApiHandlerField.setAccessible(true);
        groupApiHandlerField.set(controller, mockGroupApiHandler);

        User user = new User("KollAppControllerUserTest", "password");
        user.addUserGroup("TestGroup");
        controller.setUser(user);
        controller.initializeToDoList(user);
        controller.populateGroupView(user.getUserGroups());

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    @DisplayName("Verify '+' button presence and label")
    public void shouldContainAddButton(FxRobot robot) {
        Button addButton = robot.lookup("#addButton").queryAs(Button.class);
        Assertions.assertThat(addButton).isNotNull();
        Assertions.assertThat(addButton).hasText("+ Add task");
    }

    @Test
    @DisplayName("Logout should navigate to login screen")
    public void shouldOpenLoginScreenOnLogout(FxRobot robot) {
        robot.clickOn("#logoutButton");
        robot.lookup(".root").queryAs(Parent.class);
        Assertions.assertThat(robot.window("Login")).isShowing();
    }

    @Test
    @DisplayName("Table view should contain correct columns with headers")
    public void shouldHaveCorrectTableColumns(FxRobot robot) {
        @SuppressWarnings("unchecked")
        TableView<Task> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        Assertions.assertThat(tableView).isNotNull();
        ObservableList<TableColumn<Task, ?>> columns = tableView.getColumns();
        Assertions.assertThat(columns).hasSize(5);
        Assertions.assertThat(columns.get(4).getText()).isEqualTo("✓");
        Assertions.assertThat(columns.get(0).getText()).isEqualTo("Task");
        Assertions.assertThat(columns.get(1).getText()).isEqualTo("Date");
        Assertions.assertThat(columns.get(2).getText()).isEqualTo("Description");
        Assertions.assertThat(columns.get(3).getText()).isEqualTo("Priority");
    }

    @Test
    @DisplayName("Toggle completed and pending tasks label")
    public void shouldToggleCompletedTasksLabel(FxRobot robot) {
        Label completedLabel = robot.lookup("#completedLabel").queryAs(Label.class);
        Assertions.assertThat(completedLabel).hasText("Completed Tasks");
        robot.clickOn("#completedLabel");
        Assertions.assertThat(completedLabel).hasText("Pending Tasks");
        robot.clickOn("#completedLabel");
        Assertions.assertThat(completedLabel).hasText("Completed Tasks");
    }

    @Test
    @DisplayName("Group management buttons should be visible and enabled")
    public void shouldHaveGroupManagementButtons(FxRobot robot) {
        Button createGroupButton = robot.lookup("#createGroupButton").queryAs(Button.class);
        Button addUserToGroupButton = robot.lookup("#addUserToGroupButton").queryAs(Button.class);
        Assertions.assertThat(createGroupButton).isNotNull();
        Assertions.assertThat(addUserToGroupButton).isNotNull();
        Assertions.assertThat(createGroupButton).hasText("New group");
        Assertions.assertThat(addUserToGroupButton).hasText("Add member");
        Assertions.assertThat(createGroupButton.isDisabled()).isFalse();
        Assertions.assertThat(addUserToGroupButton.isDisabled()).isFalse();
    }

    @Test
    @DisplayName("My tasks label updates the current viewing path")
    public void shouldHandleMyTasksLabelCorrectly(FxRobot robot) {
        Label personalLabel = robot.lookup("#personal").queryAs(Label.class);
        Label currentlyViewingPath = robot.lookup("#currentlyViewingPath").queryAs(Label.class);
        Assertions.assertThat(personalLabel).isNotNull();
        Assertions.assertThat(personalLabel).hasText("My tasks");
        robot.clickOn("#personal");
        Assertions.assertThat(currentlyViewingPath).hasText("Currently Viewing: KollAppControllerUserTest → Pending Tasks");
    }

    @Test
    @DisplayName("Group options pane visibility should toggle correctly")
    public void shouldManageGroupOptionsPaneVisibility(FxRobot robot) {
        Pane groupOptionsPane = robot.lookup("#groupOptionsPane").queryAs(Pane.class);
        Button groupChatButton = robot.lookup("#groupChatButton").queryAs(Button.class);
        Button openExpenseButton = robot.lookup("#openExpenseButton").queryAs(Button.class);
        Label groupOptionsLabel = robot.lookup("#groupOptionsLabel").queryAs(Label.class);
        Assertions.assertThat(groupOptionsPane.isVisible()).isFalse();
        Assertions.assertThat(groupChatButton).hasText("Open chat");
        Assertions.assertThat(openExpenseButton).hasText("Show expenses");
        Assertions.assertThat(groupOptionsLabel).hasText("Group Options");
    }

    @Test
    @DisplayName("Hovering over Add Task button should increase its scale")
    public void shouldChangeScaleOnHoverAddButton(FxRobot robot) {
        Button addButton = robot.lookup("#addButton").queryAs(Button.class);
        double initialScaleX = addButton.getScaleX();
        double initialScaleY = addButton.getScaleY();
        robot.moveTo(addButton);
        robot.sleep(1000);
        Assertions.assertThat(addButton.getScaleX()).isGreaterThan(initialScaleX);
        Assertions.assertThat(addButton.getScaleY()).isGreaterThan(initialScaleY);
    }

    @Test
    @DisplayName("Clicking Open chat should display Group Chat window")
    public void shouldOpenGroupChatWindow(FxRobot robot) {
        robot.clickOn("TestGroup");
        robot.clickOn("#groupChatButton");
        Assertions.assertThat(robot.window("TestGroup discussion")).isShowing();
    }

    @Test
    @DisplayName("Removing completed task from Completed Tasks view should update table")
    public void shouldRemoveTaskWhenMarkedAsCompleted(FxRobot robot) {
        TableView<Task> tableView = robot.lookup("#tableView").queryAs(TableView.class);
        Assertions.assertThat(tableView.getItems()).hasSize(1);
        robot.clickOn(lookup -> lookup.getStyleClass().contains("check-box"));
        robot.sleep(500);
        robot.clickOn("#completedLabel");
        robot.sleep(500);
        tableView = robot.lookup("#tableView").queryAs(TableView.class);
        Assertions.assertThat(tableView.getItems()).hasSize(1);
        robot.clickOn(lookup -> lookup.getStyleClass().contains("check-box"));
        robot.sleep(500);
        Assertions.assertThat(tableView.getItems()).hasSize(0);
    }
}
