package ui;

import core.Expense;
import core.User;
import core.UserGroup;
import api.ExpenseApiHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
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

    private User user;
    private UserGroup group;
    private ExpenseApiHandler expenseApiHandler;
    private ObservableList<Expense> expenseList;

    public ExpenseController() {
        this.expenseApiHandler = new ExpenseApiHandler();
    }

    public void initializeExpenseController(User user, UserGroup group) {
        this.user = user;
        this.group = group;

        // Initialize columns
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paidByColumn.setCellValueFactory(new PropertyValueFactory<>("paidBy"));

        // Load expenses
        if (group != null) {
            List<Expense> expenses = expenseApiHandler.loadGroupExpenses(group);
            expenseList = FXCollections.observableArrayList(expenses);
        } else {
            // Load user's personal expenses
            List<Expense> expenses = expenseApiHandler.loadUserExpenses(user);
            expenseList = FXCollections.observableArrayList(expenses);
        }

        expenseTableView.setItems(expenseList);
    }

    @FXML
    public void addExpense() {
        // Open AddNewExpense window
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddNewExpense.fxml"));
            Parent root = fxmlLoader.load();

            AddNewExpenseController controller = fxmlLoader.getController();
            controller.initializeAddNewExpenseController(user, group, this);

            Stage stage = new Stage();
            stage.setTitle("Add New Expense");
            stage.setScene(new Scene(root));

            // Set the stage as modal
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void refreshExpenses() {
        // Reload expenses and refresh table
        if (group != null) {
            List<Expense> expenses = expenseApiHandler.loadGroupExpenses(group);
            expenseList.setAll(expenses);
        } else {
            List<Expense> expenses = expenseApiHandler.loadUserExpenses(user);
            expenseList.setAll(expenses);
        }
        expenseTableView.refresh();
    }
}
