package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import api.ToDoListApiHandler;
import core.Expense;
import core.ToDoList;
import core.User;
import core.UserGroup;

/**
 * Controller class for managing expenses in a group.
 */
public class ExpenseController {

    @FXML
    private ScrollPane viewMessagePane;

    @FXML
    private VBox vboxMessages;

    @FXML
    private Button addExpense;

    private User user;
    private UserGroup group;
    private ToDoList toDoList;
    private ToDoListApiHandler toDoListApiHandler = new ToDoListApiHandler();

    /**
     * Initializes the ExpenseController with the current user and group.
     *
     * @param user  The current user
     * @param group The group for which expenses are managed
     */
    public void initializeExpenseController(User user, UserGroup group) {
        this.user = user;
        this.group = group;

        // Load the group's ToDoList
        this.toDoList = toDoListApiHandler.loadGroupToDoList(group);

        // Display expenses
        updateExpenseView();
    }

    /**
     * Returns the group associated with this controller.
     *
     * @return The UserGroup object.
     */
    public UserGroup getGroup() {
        return this.group;
    }

    /**
     * Updates the expense view by populating the VBox with expense items.
     */
    @FXML
    public void updateExpenseView() {
        // Clear the current items in the VBox
        vboxMessages.getChildren().clear();

        // Get the list of expenses
        List<Expense> expenses = toDoList.getExpenses();

        for (Expense expense : expenses) {
            HBox expenseItem = createExpenseItem(expense);
            vboxMessages.getChildren().add(expenseItem);
        }
    }

    /**
     * Creates an HBox representing a single expense item.
     *
     * @param expense The expense to display
     * @return An HBox containing the expense details
     */
    private HBox createExpenseItem(Expense expense) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5));
        hbox.setStyle("-fx-border-color: lightgrey; -fx-border-width: 0 0 1 0;"); // Add a bottom border

        // Create labels for expense details
        Label descriptionLabel = new Label(expense.getDescription());
        descriptionLabel.setPrefWidth(200);

        Label amountLabel = new Label(String.format("$%.2f", expense.getAmount()));
        amountLabel.setPrefWidth(80);

        Label paidByLabel = new Label("Paid by: " + expense.getPaidBy());
        paidByLabel.setPrefWidth(100);

        // Optional: Add a remove button
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            // Remove the expense from the ToDoList
            toDoList.removeExpense(expense);

            // Update the persistence layer
            toDoListApiHandler.updateGroupToDoList(group, toDoList);

            // Refresh the expense view
            updateExpenseView();
        });

        hbox.getChildren().addAll(descriptionLabel, amountLabel, paidByLabel, removeButton);

        return hbox;
    }

    /**
     * Opens a window to add a new expense.
     */
    @FXML
    public void addExpense() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddExpense.fxml"));
            Parent root = fxmlLoader.load();

            AddNewExpenseController addExpenseController = fxmlLoader.getController();
            addExpenseController.initializeAddExpenseController(user, toDoList, this);

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

    /**
     * Refreshes the list of expenses displayed.
     */
    public void refreshExpenses() {
        // Reload the ToDoList to get the updated expenses
        this.toDoList = toDoListApiHandler.loadGroupToDoList(group);
        updateExpenseView();
    }
}
