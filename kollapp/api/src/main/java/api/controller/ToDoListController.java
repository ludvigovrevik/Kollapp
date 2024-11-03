package api.controller;

import api.service.ToDoListService;
import core.ToDoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/todolists")
public class ToDoListController {

    @Autowired
    private ToDoListService toDoListService;

    /**
     * Loads the to-do list for a specific user.
     * GET /todolists/{username}
     *
     * @param username the username of the user
     * @return the to-do list of the user
     */
    @GetMapping("/{username}")
    public ResponseEntity<ToDoList> loadToDoList(@PathVariable String username) {
        try {
            ToDoList toDoList = toDoListService.loadToDoList(username);
            return ResponseEntity.ok(toDoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Assigns a new to-do list to a specific user.
     * POST /todolists/{username}
     *
     * @param username the username of the user
     * @return HTTP status indicating the outcome
     */
    @PostMapping("/{username}")
    public ResponseEntity<Void> assignToDoList(@PathVariable String username) {
        try {
            toDoListService.assignToDoList(username);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates the to-do list for a specific user.
     * PUT /todolists/{username}
     *
     * @param username the username of the user
     * @param toDoList the updated to-do list
     * @return HTTP status indicating the outcome
     */
    @PutMapping("/{username}")
    public ResponseEntity<Void> updateToDoList(@PathVariable String username, @RequestBody ToDoList toDoList) {
        try {
            toDoListService.updateToDoList(username, toDoList);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Loads the to-do list for a specific user group.
     * GET /todolists/groups/{groupName}
     *
     * @param groupName the name of the user group
     * @return the to-do list of the group
     */
    @GetMapping("/groups/{groupName}")
    public ResponseEntity<ToDoList> loadGroupToDoList(@PathVariable String groupName) {
        try {
            ToDoList toDoList = toDoListService.loadGroupToDoList(groupName);
            return ResponseEntity.ok(toDoList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Updates the to-do list for a specific user group.
     * PUT /todolists/groups/{groupName}
     *
     * @param groupName the name of the user group
     * @param toDoList  the updated to-do list
     * @return HTTP status indicating the outcome
     */
    @PutMapping("/groups/{groupName}")
    public ResponseEntity<Void> updateGroupToDoList(@PathVariable String groupName, @RequestBody ToDoList toDoList) {
        try {
            toDoListService.updateGroupToDoList(groupName, toDoList);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
