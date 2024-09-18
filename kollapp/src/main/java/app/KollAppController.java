package app;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller class for the KollApp application.
 */

public class KollAppController {

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private TextField taskInputField;

    @FXML 
    private DatePicker datePicker;

    private ToDoList toDoList;

    /**
     * Initializes the controller class.
     * This method is called after the FXML file has been loaded.
     */

    public void initialize() {
        toDoList = new ToDoList();

        // Bind the ListView items to the toDoList tasks.
        taskListView.setItems(toDoList.getTasks());
    }


    /**
     * Handles the action of adding a new task.
     * This method is called when the user clicks the "+" button from the UI.
     * It retrieves the task description and date from the input fields and creates a new Task object.
     * The new task is then added to the to-do list, and clears the input fields.
     */
    @FXML
    public void handleAddTask() {
        if (!taskInputField.getText().isEmpty()) {
            String taskDescription = taskInputField.getText();
            LocalDate dateTime = datePicker.getValue();
            Task newTask = new Task(taskDescription, dateTime);

            toDoList.addTask(newTask);


            taskInputField.clear();
            datePicker.setValue(null);
        }
    }

    /**
     * Handles the action of removing a task.
     * This method is called when the "-" button from the UI is clicked.
     * It removes the selected task from the to-do list.
     */

    @FXML
    public void handleRemoveTask() {
        int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
    
        // Checks if a task is selected before removing it.
        if (selectedIndex >= 0) {
            toDoList.removeTask(selectedIndex);
        } else {
            System.out.println("Ingen oppgave var markert");
        }
    }
}
