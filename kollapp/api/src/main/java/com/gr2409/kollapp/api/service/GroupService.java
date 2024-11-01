package com.gr2409.kollapp.api.service;

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

    private String groupToDoList = Paths.get("kollapp", "persistence", "src", "main", "java",
            "persistence", "grouptodolists") + File.separator;
    private String groupPath = Paths.get("kollapp", "persistence", "src", "main", "java",
            "persistence", "groups") + File.separator;

    @Autowired
    public GroupService() {
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
        User user = userService.getUser(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        groupHandler.assignUserToGroup(user, groupName);
    }

    public void updateGroup(UserGroup group) {
        try {
            groupHandler.updateGroup(group);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update group");
        }
    }

    public boolean groupExists(String groupName) {
        return groupHandler.groupExists(groupName);
    }
}
