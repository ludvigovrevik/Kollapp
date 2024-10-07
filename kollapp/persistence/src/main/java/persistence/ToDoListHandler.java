package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.ToDoList;
import core.User;

public class ToDoListHandler {
    private static final String TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
    private static ObjectMapper mapper = new ObjectMapper();

    public static ToDoList loadToDoList(User user) {
        File file = new File(TODOLIST_PATH + user.getUsername() + ".json");
        mapper.registerModule(new JavaTimeModule()); // Register the module
        
        if (!file.exists()) {
            System.out.println("File not found for user: " + user.getUsername());
            return new ToDoList(); // Return an empty list instead of null if file doesn't exist
        }
        
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for user: " + user.getUsername());
            e.printStackTrace();
        }
        return new ToDoList();
    }


    public static void updateToDoList(User user, ToDoList toDoList) {
        File file = new File(TODOLIST_PATH + user.getUsername() + ".json");
        try {
            mapper.registerModule(new JavaTimeModule()); // Register the module
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
