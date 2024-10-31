package com.gr2409.kollapp.api.controller;

import com.gr2409.kollapp.api.service.UserService;
import core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
    
   // POST /users
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
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

    // // POST /users/login
    // @PostMapping("/login")
    // public User loadUser(@RequestBody LoginRequest loginRequest) {
    //     return userService.loadUser(loginRequest.getUsername(), loginRequest.getPassword());
    // }

    

    // POST /users/validate
    // @PostMapping("/validate")
    // public boolean confirmNewValidUser(@RequestBody UserValidationRequest userValidationRequest) {
    //     return userService.confirmNewValidUser(
    //         userValidationRequest.getUsername(),
    //         userValidationRequest.getPassword(),
    //         userValidationRequest.getConfirmPassword()
    //     );
    // }
}


