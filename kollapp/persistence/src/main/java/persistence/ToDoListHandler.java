package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.ToDoList;
import core.User;

public class ToDoListHandler {
    private final String TODOLIST_PATH;
    private ObjectMapper mapper;

    // Default constructor uses the original path
    public ToDoListHandler() {
        this.TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "todolists").toString() + File.separator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Register the module
    }

    // New constructor that accepts a custom path
    public ToDoListHandler(String todolistPath) {
        this.TODOLIST_PATH = todolistPath;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Register the module
    }

    public void assignToDoList(User user) {
        ToDoList toDoList = new ToDoList();
        File file = new File(TODOLIST_PATH + user.getUsername() + ".json");
        try {
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ToDoList loadToDoList(User user) {
        File file = new File(TODOLIST_PATH + user.getUsername() + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("To-do list file does not exist for user: " + user.getUsername());
        }
        try {
            return mapper.readValue(file, ToDoList.class);
        } catch (IOException e) {
            System.out.println("Failed to load to-do list for user: " + user.getUsername());
            throw new IllegalArgumentException(e);
        }
    }


    public void updateToDoList(User user, ToDoList toDoList) {
        File file = new File(TODOLIST_PATH + user.getUsername() + ".json");
        try {
            mapper.registerModule(new JavaTimeModule()); // Register the module
            mapper.writeValue(file, toDoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
