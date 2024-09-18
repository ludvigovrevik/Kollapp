package app;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class KollAppController {

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private TextField taskInputField;

    @FXML 
    private DatePicker datePicker;

    private ToDoList toDoList;

    public void initialize() {
        toDoList = new ToDoList();

        // Bind the ListView items to the toDoList tasks
        taskListView.setItems(toDoList.getTasks());
    }

    @FXML
    public void handleAddTask() {
        if (!taskInputField.getText().isEmpty()) {
            String taskDescription = taskInputField.getText();
            LocalDate dateTime = datePicker.getValue();
            Task newTask = new Task(taskDescription, dateTime);

            toDoList.addTask(newTask);


            taskInputField.clear();
            // TODO:clear datePicker
            datePicker.setValue(null);

        }
    }

    @FXML
    public void handleRemoveTask() {
        int selectedIndex = taskListView.getSelectionModel().getSelectedIndex();
    
    // Skjekker om en task er selected
    if (selectedIndex >= 0) {
        toDoList.removeTask(selectedIndex);
    } else {
        System.out.println("Ingen oppgave var markert");
    }
    }
}
