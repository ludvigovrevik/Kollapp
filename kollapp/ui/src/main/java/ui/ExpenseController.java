package ui;

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    private Button addExpense;

    private User user;
    private UserGroup group;
    private ExpenseApiHandler expenseApiHandler;
    private List<Expense> expenses;

    public void initializeExpenseController(User user, UserGroup group) {
        this.user = user;
        this.group = group;
        this.expenseApiHandler = new ExpenseApiHandler();

        // Load expenses
        if (group != null) {
            this.expenses = expenseApiHandler.loadGroupExpenses(group);
        } else {
            this.expenses = expenseApiHandler.loadUserExpenses(user);
        }

        // Initialize table columns using standard getter methods
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        paidByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPaidBy()));

        // Set data
        expenseTableView.getItems().setAll(expenses);
    }

    @FXML
    public void addExpense() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddExpense.fxml"));
            Parent root = fxmlLoader.load();

            AddNewExpenseController addExpenseController = fxmlLoader.getController();
            addExpenseController.initializeAddExpenseController(user, group, this);

            Stage stage = new Stage();
            stage.setTitle("Add Expense");
            stage.setScene(new Scene(root));

            // Set the stage as modal, blocking user input to other windows
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println("Error loading AddExpense.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshExpenses() {
        if (group != null) {
            this.expenses = expenseApiHandler.loadGroupExpenses(group);
        } else {
            this.expenses = expenseApiHandler.loadUserExpenses(user);
        }
        expenseTableView.getItems().setAll(expenses);
    }
}
