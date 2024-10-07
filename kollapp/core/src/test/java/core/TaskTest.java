package core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {
    
    @Test
    void testTaskInitialization() {
        String taskName = "Complete the report";
        Task task = new Task(taskName);

        Assertions.assertEquals(taskName, task.getTaskName(), "Description should match the input");
        Assertions.assertFalse(task.isCompleted(), "New task should not be completed");
    }

    @Test
    void testSetDescription() {
        Task task = new Task("Initial Description");
        String newDescription = "Updated Description";
        task.setDescription(newDescription);

        Assertions.assertEquals(newDescription, task.getDescription(), "Description should be updated");
    }

    @Test
    void testSetCompleted() {
        Task task = new Task("Complete unit tests");
        task.setCompleted(true);

        Assertions.assertTrue(task.isCompleted(), "Task should be marked as completed");
    }
}
