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

    /**
     * Handles GET requests to load expenses for a specific group.
     *
     * @param groupName the name of the group whose expenses are to be loaded
     * @return a ResponseEntity containing a list of expenses for the specified group,
     *         or an INTERNAL_SERVER_ERROR status if an exception occurs
     */
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

    /**
     * Updates the expenses for a specific group.
     *
     * @param groupName the name of the group whose expenses are to be updated
     * @param expenses the list of expenses to update for the group
     * @return a ResponseEntity with status 200 (OK) if the update is successful,
     *         or status 400 (Bad Request) if there is an IllegalArgumentException
     */
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
