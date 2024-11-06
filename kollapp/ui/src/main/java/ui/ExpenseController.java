package ui;

import api.ExpenseApiHandler;
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
    private ExpenseApiHandler expenseApiHandler = new ExpenseApiHandler();
    private ObservableList<Expense> expenses = FXCollections.observableArrayList();

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

    private void updateExpenses() {
        boolean success = expenseApiHandler.updateGroupExpenses(groupInView, expenses);
        if (success) {
            refreshTable();
        } else {
            // Handle update failure if necessary
        }
    }

    private void refreshTable() {
        expenseTableView.refresh();
        updateTotalOwed();
    }

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

    @FXML
    private void addExpense() {
        // Open the AddNewExpense window
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("AddNewExpense.fxml"));
            javafx.scene.Parent root = fxmlLoader.load();

            AddNewExpenseController addNewExpenseController = fxmlLoader.getController();
            addNewExpenseController.initializeController(currentUser, groupInView, this);

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
