package ui;

import core.Expense;
import core.ToDoList;
import core.User;
import core.UserGroup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import api.ToDoListApiHandler;

/**
 * Controller class for adding a new expense.
 */
public class AddNewExpenseController {

    @FXML
    private TextField expenseNameField;

    @FXML
    private TextField expenseField;

    @FXML
    private Label errorLabel;

    private User user;
    private ToDoList toDoList;
    private ExpenseController expenseController;
    private ToDoListApiHandler toDoListApiHandler;

    /**
     * Initializes the controller with necessary data.
     *
     * @param user              The current user
     * @param toDoList          The ToDoList to which the expense will be added
     * @param expenseController The parent controller to refresh after adding
     */
    public void initializeAddExpenseController(User user, ToDoList toDoList, ExpenseController expenseController) {
        this.user = user;
        this.toDoList = toDoList;
        this.expenseController = expenseController;
        this.toDoListApiHandler = new ToDoListApiHandler();
    }

    /**
     * Handles the action of adding a new expense.
     *
     * @param event The event triggered by clicking the "Add Expense" button.
     */
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
        toDoList.addExpense(newExpense);

        // Save the updated ToDoList
        UserGroup group = expenseController.getGroup();
        toDoListApiHandler.updateGroupToDoList(group, toDoList);

        // Refresh the expense list in the parent controller
        expenseController.refreshExpenses();

        // Close the window
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
