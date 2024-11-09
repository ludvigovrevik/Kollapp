package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.Task;
import core.ToDoList;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Unit tests for the AddNewTaskController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class AddNewTaskControllerIT {

    private ToDoList mockToDoList;
    private KollAppController mockKollAppController;

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
     * Initializes the controller and the stage for testing.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddNewTask.fxml"));
        Parent root = loader.load();

        AddNewTaskController controller = loader.getController();
        mockToDoList = mock(ToDoList.class);
        mockKollAppController = mock(KollAppController.class);
        User mockUser = mock(User.class);

        controller.initializeTaskWindow(mockUser, mockToDoList, mockKollAppController);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Tests adding a new task with valid inputs.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding a new task successfully")
    @Tag("task")
    void testAddNewTask_Success(FxRobot robot) {
        robot.clickOn("#taskNameField").write("Test Task");
        robot.clickOn("#taskDescriptionField").write("This is a test description.");
        robot.clickOn("#priorityField").clickOn("High");

        LocalDate fixedDate = LocalDate.of(2024, 4, 27);
        robot.interact(() -> {
            DatePicker datePicker = robot.lookup("#datePicker").queryAs(DatePicker.class);
            datePicker.setValue(fixedDate);
        });


        robot.clickOn("Add task");

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(mockToDoList).addTask(taskCaptor.capture());

        Task addedTask = taskCaptor.getValue();
        assertEquals("Test Task", addedTask.getTaskName());
        assertEquals("This is a test description.", addedTask.getDescription());
        assertEquals("High", addedTask.getPriority());
        assertEquals(fixedDate, addedTask.getDateTime());

        // verify(mockKollAppController).updateGrid();
    }

    /**
     * Tests that adding a task without a task name does not succeed.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding a task with an empty task name")
    @Tag("task")
    void testAddNewTask_EmptyTaskName(FxRobot robot) {
        robot.clickOn("#taskDescriptionField").write("Description without task name.");
        robot.clickOn("#priorityField").clickOn("Medium");

        robot.interact(() -> {
            DatePicker datePicker = robot.lookup("#datePicker").queryAs(DatePicker.class);
            datePicker.setValue(LocalDate.now());
        });

        robot.clickOn("Add task");

        verify(mockToDoList, never()).addTask(any(Task.class));
        // verify(mockKollAppController, never()).updateGrid();

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertNotNull(errorLabel, "Error label not found!");
        assertEquals("Task cannot be null.", errorLabel.getText());
    }
}
