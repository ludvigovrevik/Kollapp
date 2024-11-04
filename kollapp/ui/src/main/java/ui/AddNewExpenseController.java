package ui;

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AddNewExpenseController {

    @FXML
    private TextField expenseNameField;

    @FXML
    private TextField expenseField;

    @FXML
    private Label errorLabel;

    private User user;
    private UserGroup group;
    private ExpenseApiHandler expenseApiHandler;
    private ExpenseController expenseController;

    public void initializeAddExpenseController(User user, UserGroup group, ExpenseController expenseController) {
        this.user = user;
        this.group = group;
        this.expenseController = expenseController;
        this.expenseApiHandler = new ExpenseApiHandler();
    }

    @FXML
    public void handleAddExpense(ActionEvent event) {
        String description = expenseNameField.getText();
        String amountText = expenseField.getText();

        if (description == null || description.isEmpty()) {
            errorLabel.setText("Expense name cannot be empty.");
            return;
        }
        if (amountText == null || amountText.isEmpty()) {
            errorLabel.setText("Expense amount cannot be empty.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount must be a valid number.");
            return;
        }

        Expense newExpense = new Expense(description, amount, user.getUsername());

        // Load existing expenses
        List<Expense> expenses;
        if (group != null) {
            expenses = expenseApiHandler.loadGroupExpenses(group);
        } else {
            expenses = expenseApiHandler.loadUserExpenses(user);
        }

        // Add new expense
        expenses.add(newExpense);

        // Save updated expenses
        if (group != null) {
            expenseApiHandler.updateGroupExpenses(group, expenses);
        } else {
            expenseApiHandler.updateUserExpenses(user, expenses);
        }

        // Refresh the expense list in the parent controller
        expenseController.refreshExpenses();

        // Close the window
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
