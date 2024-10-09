package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxRobot;
import java.time.LocalDate;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.Task;
import core.ToDoList;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;

@ExtendWith(ApplicationExtension.class)
public class AddNewTaskControllerTest {

    private AddNewTaskController controller;
    private ToDoList mockToDoList;
    private KollAppController mockKollAppController;
    private User mockUser;

    @Start
    public void start(Stage stage) throws Exception {
        // Load the FXML and get the scene for the test
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddNewTask.fxml"));
        Parent root = loader.load();

        // Get the controller instance from the loader
        controller = loader.getController();

        // Mock dependencies
        mockToDoList = mock(ToDoList.class);
        mockKollAppController = mock(KollAppController.class);
        mockUser = mock(User.class);

        // Inject mocks into the controller
        controller.initializeTaskWindow(mockUser, mockToDoList, mockKollAppController);

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void testAddNewTask_Success(FxRobot robot) {
        // Simulate user input
        robot.clickOn("#taskNameField").write("Test Task");
        robot.clickOn("#taskDescriptionField").write("This is a test description.");
        robot.clickOn("#priorityField").clickOn("High");

        // Set the DatePicker's value directly
        robot.interact(() -> {
            DatePicker datePicker = robot.lookup("#datePicker").queryAs(DatePicker.class);
            datePicker.setValue(LocalDate.now());
        });

        // Click the "Add task" button
        robot.clickOn("Add task");

        // Capture the Task object passed to addTask
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(mockToDoList).addTask(taskCaptor.capture());

        // Verify the properties of the created Task
        Task addedTask = taskCaptor.getValue();
        assertEquals("Test Task", addedTask.getTaskName());
        assertEquals("This is a test description.", addedTask.getDescription());
        assertEquals("High", addedTask.getPriority());
        assertEquals(LocalDate.now(), addedTask.getDateTime());

        // Verify that updateGrid was called
        verify(mockKollAppController).updateGrid();
    }

    @Test
    void testAddNewTask_EmptyTaskName(FxRobot robot) {
        // Leave task name empty
        robot.clickOn("#taskDescriptionField").write("Description without task name.");
        robot.clickOn("#priorityField").clickOn("Medium");

        // Set the DatePicker's value directly
        robot.interact(() -> {
            DatePicker datePicker = robot.lookup("#datePicker").queryAs(DatePicker.class);
            datePicker.setValue(LocalDate.now());
        });

        // Click the "Add task" button
        robot.clickOn("Add task");

        // Verify that addTask was not called
        verify(mockToDoList, never()).addTask(any(Task.class));

        // Verify that updateGrid was not called
        verify(mockKollAppController, never()).updateGrid();
    }
}