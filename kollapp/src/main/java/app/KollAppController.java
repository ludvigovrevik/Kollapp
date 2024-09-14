package app;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class KollAppController {

    @FXML
    private ListView<Task> taskListView;

    @FXML
    private TextField taskInputField;

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
            Task newTask = new Task(taskDescription);
            toDoList.addTask(newTask);

            taskInputField.clear();
        }
    }
}
