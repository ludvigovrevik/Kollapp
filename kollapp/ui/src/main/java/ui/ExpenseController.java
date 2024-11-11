package ui;

import core.Expense;
import core.User;
import core.UserGroup;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

import api.ExpenseApiHandler;

public class ExpenseController {

    @FXML
    private TableView<Expense> expenseTableView;
    @FXML
    private TableColumn<Expense, String> descriptionColumn;
    @FXML
    private TableColumn<Expense, Double> amountColumn;
    @FXML
    private TableColumn<Expense, String> paidByColumn;
    @FXML
    private TableColumn<Expense, Double> sharePerPersonColumn;
    @FXML
    private TableColumn<Expense, String> statusColumn;

    @FXML
    private Button addExpenseButton;

    @FXML
    private Label totalOwedLabel;

    private User currentUser;
    private UserGroup groupInView;
    private ExpenseApiHandler expenseApiHandler;
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();
    
    // Add a constructor that initializes the handler
    public ExpenseController() {
        this.expenseApiHandler = new ExpenseApiHandler();
    }

    public void setExpenseApiHandler(ExpenseApiHandler expenseApiHandler) {
        this.expenseApiHandler = expenseApiHandler;
    }
    /**
     * Initializes the ExpenseController with the given user and user group.
     * Sets up the table columns and their cell value factories, including a custom cell factory
     * for the status and settlement column. Loads and populates the table with expenses.
     *
     * @param user  the current user
     * @param group the user group in view
     */
    public void initializeExpenseController(User user, UserGroup group) {
        this.currentUser = user;
        this.groupInView = group;

        // Initialize table columns
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paidByColumn.setCellValueFactory(new PropertyValueFactory<>("paidBy"));
        sharePerPersonColumn.setCellValueFactory(expense -> new SimpleDoubleProperty(expense.getValue().getSharePerPerson()).asObject());

        // Custom cell for status and settlement
        statusColumn.setCellFactory(column -> new TableCell<>() {
            private final Button settleButton = new Button("Settle");

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Expense expense = getTableView().getItems().get(getIndex());
                    if (expense.getPaidBy().equals(currentUser.getUsername())) {
                        setText("You paid");
                        setGraphic(null);
                    } else if (expense.hasParticipantSettled(currentUser.getUsername())) {
                        setText("Settled");
                        setGraphic(null);
                    } else {
                        setText("Owe: $" + String.format("%.2f", expense.getSharePerPerson()));
                        setGraphic(settleButton);
                        settleButton.setOnAction(event -> {
                            expense.settleParticipant(currentUser.getUsername());
                            updateExpenses();
                        });
                    }
                }
            }
        });

        // Load expenses and populate table
        loadExpenses();
    }

    /**
     * Loads the expenses for the current group in view.
     * If expenses are successfully loaded, updates the expenses list and table view,
     * refreshes the table, and updates the total amount owed.
     * If no expenses are loaded, clears the expenses list and updates the total amount owed.
     */
    private void loadExpenses() {
        List<Expense> loadedExpenses = expenseApiHandler.loadGroupExpenses(groupInView);
        if (loadedExpenses != null) {
            expenses.setAll(loadedExpenses);
            expenseTableView.setItems(expenses);
            refreshTable();
            updateTotalOwed();
        } else {
            expenses.clear();
            expenseTableView.setItems(expenses);
            updateTotalOwed();
        }
    }

    public void reloadExpenses() {
        loadExpenses();
    }

    /**
     * Updates the expenses for the current group in view by calling the expense API handler.
     * If the update is successful, it reloads the expenses from the server.
     * If the update fails, it handles the failure as necessary.
     */
    private void updateExpenses() {
        boolean success = expenseApiHandler.updateGroupExpenses(groupInView, expenses);
        if (success) {
            loadExpenses(); // Reload expenses from the server
        } else {
            // Handle update failure if necessary
        }
    }

    /**
     * Refreshes the expense table view and updates the total amount owed.
     * This method should be called whenever the data in the table needs to be reloaded
     * or when the total owed amount needs to be recalculated.
     */
    private void refreshTable() {
        expenseTableView.refresh();
        updateTotalOwed();
    }

    /**
     * Updates the total amount owed by the current user.
     * This method iterates through the list of expenses and calculates the total amount
     * that the current user owes. It checks if the current user is a participant in the expense,
     * if the expense was not paid by the current user, and if the current user has not settled
     * the expense. If all conditions are met, the user's share of the expense is added to the total owed.
     * The total owed amount is then displayed in the totalOwedLabel.
     */
    private void updateTotalOwed() {
        double totalOwed = 0.0;
        for (Expense expense : expenses) {
            if (expense.getParticipants().contains(currentUser.getUsername())
                    && !expense.getPaidBy().equals(currentUser.getUsername())
                    && !expense.hasParticipantSettled(currentUser.getUsername())) {
                totalOwed += expense.getSharePerPerson();
            }
        }
        totalOwedLabel.setText("Total Owed: $" + String.format("%.2f", totalOwed));
    }

    /**
     * Opens the AddNewExpense window to allow the user to add a new expense.
     * This method loads the AddNewExpense.fxml file, initializes the controller
     * with the current user and group information, and displays the window in a 
     * modal stage. After the window is closed, it refreshes the list of expenses.
     *
     * @FXML annotation indicates that this method is linked to an event handler in the FXML file.
     */
    @FXML
    private void addExpense() {
        // Open the AddNewExpense window
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("AddNewExpense.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            AddNewExpenseController addNewExpenseController = fxmlLoader.getController();
            addNewExpenseController.initializeController(currentUser, groupInView);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Add New Expense");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh expenses after adding a new one
            loadExpenses();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
