package app;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

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
     * This method is called automatically after the FXML file has been loaded.
     * It creates a new to-do list and sets up the ListView to display the tasks.
     */
    public void initialize() {
    toDoList = new ToDoList();

    taskListView.setItems(toDoList.getTasks());
    taskListView.setEditable(true);
    
    // Set up the ListView to display the tasks with a bullet point and date (if available)
    taskListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<Task>() {

        // toString() returns the text that is displayed in the ListView
        @Override
        public String toString(Task task) {
            String datePart = (task.getDateTime() != null ? "  |  " + task.getDateTime().toString() : "");
            return "• " + task.getDescription() + datePart;
        }
    
        // Method is called when the user has finished editing the task description. 
        // It updates the task object with the new description and saves the updated task list to the file.
        @Override
        public Task fromString(String string) {
            
            // Method inspired by this solution from Stack Overflow:
            // https://stackoverflow.com/questions/13264017/getting-selected-element-from-listview
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                String newDescription = string.trim();
                if (newDescription.startsWith("• ")) {
                    newDescription = newDescription.substring(2).trim();
                }

                // Remove any part of the string that looks like a date after the description
                int dateIndex = newDescription.indexOf("|");
                if (dateIndex != -1) {
                    newDescription = newDescription.substring(0, dateIndex).trim();
                }

                selectedTask.setDescription(newDescription);
                toDoList.saveTasksToFile();
                }
                return selectedTask;
            }
        }));
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
    
        // Check if a task is selected before removing it.
        if (selectedIndex >= 0) {
            toDoList.removeTask(selectedIndex);
        } else {
            System.out.println("No task selected.");
        }
    }

    // TODO implement setUser method
    public void setUser(User user) {
        // TODO
    }
}
