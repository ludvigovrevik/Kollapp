package ui;

import core.Expense;
import core.User;
import core.UserGroup;
import api.ExpenseApiHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button addExpenseButton;

    private User user;
    private UserGroup group;
    private ExpenseApiHandler expenseApiHandler;
    private ExpenseController expenseController;

    public AddNewExpenseController() {
        this.expenseApiHandler = new ExpenseApiHandler();
    }

    public void initializeAddNewExpenseController(User user, UserGroup group, ExpenseController expenseController) {
        this.user = user;
        this.group = group;
        this.expenseController = expenseController;
    }

    @FXML
    public void handleAddExpense() {
        String description = expenseNameField.getText();
        String amountStr = expenseField.getText();

        if (description.isEmpty() || amountStr.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid amount.");
            return;
        }

        Expense expense = new Expense(description, amount, user.getUsername());

        if (group != null) {
            List<Expense> expenses = expenseApiHandler.loadGroupExpenses(group);
            expenses.add(expense);
            expenseApiHandler.updateGroupExpenses(group, expenses);
        } else {
            List<Expense> expenses = expenseApiHandler.loadUserExpenses(user);
            expenses.add(expense);
            expenseApiHandler.updateUserExpenses(user, expenses);
        }

        // Close the window
        Stage stage = (Stage) addExpenseButton.getScene().getWindow();
        stage.close();

        // Refresh expenses in the main controller
        expenseController.refreshExpenses();
    }
}
