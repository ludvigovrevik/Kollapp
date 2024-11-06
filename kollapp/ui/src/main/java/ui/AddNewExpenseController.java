// File: ui/AddNewExpenseController.java

package ui;

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddNewExpenseController {

    @FXML
    private TextField expenseNameField;
    @FXML
    private TextField amountField;
    @FXML
    private Label errorLabel;

    private User currentUser;
    private UserGroup groupInView;
    private ExpenseApiHandler expenseApiHandler = new ExpenseApiHandler();
    private ExpenseController parentController;

    public void initializeController(User user, UserGroup group, ExpenseController parentController) {
        this.currentUser = user;
        this.groupInView = group;
        this.parentController = parentController;
    }

    @FXML
    private void handleAddExpense() {
        String description = expenseNameField.getText().trim();
        String amountText = amountField.getText().trim();

        if (description.isEmpty() || amountText.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a number.");
            return;
        }

        // Get participants from the group
        List<String> participants = new ArrayList<>(groupInView.getUsers());

        // Create new expense
        Expense newExpense = new Expense(description, amount, currentUser.getUsername(), participants);

        // Load existing expenses
        List<Expense> expenses = expenseApiHandler.loadGroupExpenses(groupInView);
        if (expenses == null) {
            expenses = new ArrayList<>();
        }

        expenses.add(newExpense);

        // Update expenses
        boolean success = expenseApiHandler.updateGroupExpenses(groupInView, expenses);
        if (success) {
            // Close the window
            Stage stage = (Stage) expenseNameField.getScene().getWindow();
            stage.close();
        } else {
            errorLabel.setText("Failed to add expense.");
        }
    }
}
