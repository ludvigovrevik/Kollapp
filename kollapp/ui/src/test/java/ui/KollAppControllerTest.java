package ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mockito.*;
import java.lang.reflect.Field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import api.ToDoListApiHandler;
import core.Task;
import core.ToDoList;
import core.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

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

    /**
     * Initializes the test environment by loading the Kollektiv.fxml and setting up the controller.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    private void start(Stage stage) throws Exception {
        // Mock the API handler
        ToDoListApiHandler mockApiHandler = mock(ToDoListApiHandler.class);
        when(mockApiHandler.loadToDoList(any(User.class))).thenReturn(new ToDoList());
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Kollektiv.fxml"));
        Parent root = loader.load();

        KollAppController controller = loader.getController();
        // Set the mocked API handler
        Field apiHandlerField = KollAppController.class.getDeclaredField("toDoListApiHandler");
        apiHandlerField.setAccessible(true);
        apiHandlerField.set(controller, mockApiHandler);
        
        User user = new User("KollAppControllerUserTest", "passwordd");
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
}
