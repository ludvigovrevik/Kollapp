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

    // GET /groups/{groupName}
    @GetMapping("/{groupName}")
    public ResponseEntity<UserGroup> getGroup(@PathVariable String groupName) {
        Optional<UserGroup> groupOpt = groupService.getGroup(groupName);
        return groupOpt.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // POST /groups/{username}/{groupName}
    @PostMapping("/{username}/{groupName}")
    public ResponseEntity<Void> createGroup(
            @PathVariable String username, 
            @PathVariable String groupName) {
        try {
            groupService.createGroup(username, groupName);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
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

    @GetMapping("/validate-assignment")
    public ResponseEntity<String> validateGroupAssignment(
            @RequestParam String username,
            @RequestParam String groupName) {
        try {
            String validationError = groupService.validateGroupAssignment(username, groupName);
            if (validationError == null) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body(validationError);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred during validation");
        }
    }
}
