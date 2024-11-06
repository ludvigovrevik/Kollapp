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

    // Load expenses for a group
    @GetMapping("/groups/{groupName}")
    public ResponseEntity<List<Expense>> loadGroupExpenses(@PathVariable String groupName) {
        try {
            List<Expense> expenses = expenseService.loadGroupExpenses(groupName);
            return ResponseEntity.ok(expenses);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update expenses for a group
    @PutMapping("/groups/{groupName}")
    public ResponseEntity<Void> updateGroupExpenses(@PathVariable String groupName, @RequestBody List<Expense> expenses) {
        try {
            expenseService.updateGroupExpenses(groupName, expenses);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
