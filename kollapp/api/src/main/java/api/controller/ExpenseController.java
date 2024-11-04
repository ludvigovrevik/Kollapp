package api.controller;

import api.service.ExpenseService;
import core.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Load expenses for a user
    @GetMapping("/{username}")
    public ResponseEntity<List<Expense>> loadUserExpenses(@PathVariable String username) {
        try {
            List<Expense> expenses = expenseService.loadUserExpenses(username);
            return ResponseEntity.ok(expenses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Update expenses for a user
    @PutMapping("/{username}")
    public ResponseEntity<Void> updateUserExpenses(@PathVariable String username, @RequestBody List<Expense> expenses) {
        try {
            expenseService.updateUserExpenses(username, expenses);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Similar methods for groups
    @GetMapping("/groups/{groupName}")
    public ResponseEntity<List<Expense>> loadGroupExpenses(@PathVariable String groupName) {
        try {
            List<Expense> expenses = expenseService.loadGroupExpenses(groupName);
            return ResponseEntity.ok(expenses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/groups/{groupName}")
    public ResponseEntity<Void> updateGroupExpenses(@PathVariable String groupName, @RequestBody List<Expense> expenses) {
        try {
            expenseService.updateGroupExpenses(groupName, expenses);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
