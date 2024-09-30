package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.ToDoList;
import core.User;
import core.UserGroup;

public class GroupHandler {
    private static final String TODOLIST_PATH = Paths.get("..", "persistence", "src", "main", "java", "persistence", "groups").toString() + File.separator;
    private static ObjectMapper mapper = new ObjectMapper();

    public static void createGroup(User user, String groupName) {
        UserGroup userGroup = new UserGroup(groupName);
        File file = new File(TODOLIST_PATH + groupName + ".json");
        userGroup.addUser(user.getUsername());
        userGroup.setToDoList(new ToDoList());
        try {
            mapper.writeValue(file, userGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void assignUserToGroup(User user, String groupName) {
        // TODO implement this method, which assigns a user to a group
        // Concider using UserGroup object as parameter
    }
}
