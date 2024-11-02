package api.controller;

import core.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import api.service.GroupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // DTO classes for request bodies
    public static class GroupCreationRequest {
        private String username;
        private String groupName;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
    }

    // Define a request class if not already defined
    public class AssignUserRequest {
        private String username;

        // Constructors, getters, and setters
        public AssignUserRequest() {}

        public AssignUserRequest(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // GET /groups/{groupName}
    @GetMapping("/{groupName}")
    public ResponseEntity<UserGroup> getGroup(@PathVariable String groupName) {
        Optional<UserGroup> groupOpt = groupService.getGroup(groupName);
        return groupOpt.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /groups
    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody GroupCreationRequest request) {
        try {
            groupService.createGroup(request.getUsername(), request.getGroupName());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /groups/{groupName}
    @PutMapping("/{groupName}")
    public ResponseEntity<Void> updateGroup(@PathVariable String groupName, @RequestBody UserGroup group) {
        try {
            groupService.updateGroup(group);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /groups/exists/{groupName}
    @GetMapping(value = "/exists/{groupName}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String groupExists(@PathVariable String groupName) {
        try {
            boolean exists = groupService.groupExists(groupName);
            return String.valueOf(exists);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
        }
    }

    @PostMapping(value = "/{groupName}/assignUser", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> assignUserToGroup(@PathVariable String groupName, @RequestParam String username) {
        try {
            System.out.println("Assigning user to group: " + username + " in group: " + groupName);
            groupService.assignUserToGroup(username, groupName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
