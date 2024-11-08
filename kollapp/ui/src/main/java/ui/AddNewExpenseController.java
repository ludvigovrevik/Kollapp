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
    private ExpenseApiHandler expenseApiHandler;

    public AddNewExpenseController() {
        this.expenseApiHandler = new ExpenseApiHandler(); // default constructor
    }
    
    public void setExpenseApiHandler(ExpenseApiHandler expenseApiHandler) {
        this.expenseApiHandler = expenseApiHandler;
    }
    /**
     * Initializes the controller with the specified user, user group, and parent expense controller.
     *
     * @param user the current user
     * @param group the user group in view
     * @param parentController the parent expense controller
     */
    public void initializeController(User user, UserGroup group) {
        this.currentUser = user;
        this.groupInView = group;
    }

    /**
     * Handles the action of adding a new expense.
     * <p>
     * This method retrieves the description and amount from the input fields,
     * validates them, and creates a new expense. The new expense is then added
     * to the list of existing expenses for the group. If the operation is successful,
     * the window is closed; otherwise, an error message is displayed.
     * </p>
     * <p>
     * The method performs the following steps:
     * <ul>
     *   <li>Retrieves and trims the description and amount from the input fields.</li>
     *   <li>Checks if the description or amount fields are empty and displays an error message if they are.</li>
     *   <li>Parses the amount to a double and displays an error message if the parsing fails.</li>
     *   <li>Gets the list of participants from the current group.</li>
     *   <li>Creates a new expense with the provided description, amount, current user's username, and participants.</li>
     *   <li>Loads the existing expenses for the group and adds the new expense to the list.</li>
     *   <li>Updates the group's expenses and closes the window if the update is successful; otherwise, displays an error message.</li>
     * </ul>
     * </p>
     */
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
