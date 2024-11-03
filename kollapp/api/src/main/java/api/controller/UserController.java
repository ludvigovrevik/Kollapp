package api.controller;

import core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import api.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    // GET /users/{username}
    @GetMapping("/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
    Optional<User> userOpt = userService.getUser(username);
    return userOpt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveUser(@RequestBody User user) {
        try {
            userService.saveUser(user);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // PUT /users/{username}
    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUser(@PathVariable String username) {
        try {
            userService.updateUser(username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DELETE /users/{username}
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> removeUser(@PathVariable String username) {
        try {
            userService.removeUser(username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET /users/exists/{username}
    @GetMapping(value = "/exists/{username}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String userExists(@PathVariable String username) {
        try {
            boolean exists = userService.userExists(username);
            return String.valueOf(exists);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
        }
    }

    // POST /users/login
    @PostMapping("/login")
    public ResponseEntity<User> loadUser(@RequestParam String username, @RequestParam String password) {
        return userService.loadUser(username, password)
            .map(user -> ResponseEntity.ok(user))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // POST /users/validate
    @PostMapping("/validate")
    public boolean confirmNewValidUser(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {
        return userService.confirmNewValidUser(username, password, confirmPassword);
    }

    // POST /users/validate/message
    @PostMapping("/validate/message")
    public String getUserValidationErrorMessage(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword) {
        return userService.getUserValidationErrorMessage(username, password, confirmPassword);
    }

    @PostMapping(value = "/{username}/assignGroup", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> assignGroupToUser(@PathVariable String username, @RequestParam String groupName) {
        try {
            System.out.println("Assigning group to user: " + groupName + " for user: " + username);
            userService.assignGroupToUser(username, groupName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}


