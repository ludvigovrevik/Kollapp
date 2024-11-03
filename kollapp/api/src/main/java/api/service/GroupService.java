package api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import persistence.GroupHandler;
import persistence.UserHandler;
import core.User;
import core.UserGroup;

@Service
public class GroupService {

    private final GroupHandler groupHandler;
    private final UserService userService;

    @Autowired
    public GroupService() {
        String groupToDoList = Paths.get("Prosjektet", "gr2409", "kollapp", "persistence", "src", "main", "java",
                "persistence", "grouptodolists") + File.separator;
        String groupPath = Paths.get("Prosjektet", "gr2409", "kollapp", "persistence", "src", "main", "java",
                "persistence", "groups") + File.separator;
        this.groupHandler = new GroupHandler(groupPath, groupToDoList, new UserHandler());
        this.userService = new UserService();
    }

    public Optional<UserGroup> getGroup(String groupName) {
        try {
            UserGroup group = groupHandler.getGroup(groupName);
            return Optional.of(group);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public void createGroup(String username, String groupName) {
        User user = userService.getUser(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        groupHandler.createGroup(user, groupName);
    }

    public void assignUserToGroup(String username, String groupName) {
        try {
            User user = userService.getUser(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            groupHandler.assignUserToGroup(user, groupName);
        } catch (Exception e) {
            System.err.println("Exception in groupService.assignUserToGroup: " + e.getMessage());
            throw e;
        }
    }

    public boolean groupExists(String groupName) {
        return groupHandler.groupExists(groupName);
    }
}
