package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class ExpenseControllerTest {
    
    @Mock
    private ExpenseApiHandler mockExpenseHandler;
    private User testUser;
    private UserGroup testGroup;
    private ExpenseController controller;
    private List<Expense> testExpenses;
    private Stage stage;

    // Headless mode configuration
    static private boolean headless = false;

    @BeforeAll
    static void setupHeadlessMode() {
        if(headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        MockitoAnnotations.openMocks(this);

        // Set up test data
        testUser = new User("testUser", "password");
        testGroup = new UserGroup("testGroup");
        testGroup.addUser("testUser");
        testGroup.addUser("otherUser");

        // Create test expenses
        testExpenses = createTestExpenses();

        // Load the FXML
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ui/ExpenseScreen.fxml"));
        Parent root = loader.load();
        
        // Get the controller and inject the mock
        controller = loader.getController();
        controller.setExpenseApiHandler(mockExpenseHandler);
        
        // Set up initial mock behavior
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(testExpenses);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);
        
        // Initialize the controller
        controller.initializeExpenseController(testUser, testGroup);
        
        // Show the scene
        stage.setScene(new Scene(root));
        stage.show();
        
        // Wait for JavaFX events to be processed
        WaitForAsyncUtils.waitForFxEvents();
    }

    private List<Expense> createTestExpenses() {
        List<Expense> expenses = new ArrayList<>();
        
        // Create expenses with known amounts
        Expense expense1 = new Expense("Dinner", 100.0, "testUser", Arrays.asList("testUser", "otherUser"));
        Expense expense2 = new Expense("Groceries", 80.0, "otherUser", Arrays.asList("testUser", "otherUser"));
        Expense expense3 = new Expense("Movie", 40.0, "otherUser", Arrays.asList("testUser", "otherUser"));
        
        // Set up the settled status
        expense3.settleParticipant("testUser");
        
        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        
        return expenses;
    }

    @BeforeEach
    public void setUp() {
        // Reset mocks and set up default behavior
        reset(mockExpenseHandler);
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(testExpenses);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);
    }

    // @Test
    // @DisplayName("Test settle expense button")
    // void testSettleExpense(FxRobot robot) {
    //     // Wait for table to be populated
    //     WaitForAsyncUtils.waitForFxEvents();

    //     // Get the table
    //     TableView<Expense> table = robot.lookup("#expenseTableView").queryAs(TableView.class);
    //     assertNotNull(table);

    //     // Find the unsettled expense (Groceries)
    //     Expense unsettledExpense = testExpenses.stream()
    //         .filter(e -> e.getDescription().equals("Groceries"))
    //         .findFirst()
    //         .orElseThrow();

    //     // Find the cell with the settle button
    //     @SuppressWarnings("unchecked")
    //     TableCell<Expense, String> cell = (TableCell<Expense, String>) robot.lookup(".table-cell")
    //         .match(node -> {
    //             if (node instanceof TableCell) {
    //                 TableCell<?, ?> tableCell = (TableCell<?, ?>) node;
    //                 Node graphic = tableCell.getGraphic();
    //                 return graphic instanceof Button && 
    //                        ((Button) graphic).getText().equals("Settle") &&
    //                        tableCell.getText() != null &&
    //                        tableCell.getText().contains("40.00");
    //             }
    //             return false;
    //         })
    //         .query();

    //     assertNotNull(cell, "Could not find cell with settle button");
        
    //     // Get the settle button from the cell
    //     Button settleButton = (Button) cell.getGraphic();
    //     assertNotNull(settleButton, "Settle button not found");

    //     // Click the button using its scene coordinates
    //     robot.clickOn(settleButton);
    //     WaitForAsyncUtils.waitForFxEvents();

    //     // Verify the mock interactions
    //     verify(mockExpenseHandler).updateGroupExpenses(eq(testGroup), argThat(expenses -> {
    //         if (expenses == null) return false;
    //         return expenses.stream()
    //             .filter(e -> e.getDescription().equals("Groceries"))
    //             .findFirst()
    //             .map(e -> e.hasParticipantSettled("testUser"))
    //             .orElse(false);
    //     }));

    //     verify(mockExpenseHandler, atLeastOnce()).loadGroupExpenses(any(UserGroup.class));
    // }

    @Test
    @DisplayName("Test expense table initialization")
    void testExpenseTableInitialization(FxRobot robot) {
        TableView<Expense> expenseTable = robot.lookup("#expenseTableView").queryAs(TableView.class);
        
        // Verify table is populated
        assertEquals(3, expenseTable.getItems().size());
        
        // Verify first expense details
        Expense firstExpense = expenseTable.getItems().get(0);
        assertEquals("Dinner", firstExpense.getDescription());
        assertEquals(100.0, firstExpense.getAmount());
        assertEquals("testUser", firstExpense.getPaidBy());
    }

    @Test
    @DisplayName("Test total owed calculation")
    void testTotalOwedCalculation(FxRobot robot) {
        Label totalOwedLabel = robot.lookup("#totalOwedLabel").queryAs(Label.class);
        
        // Only expense2 (Groceries) is unsettled and owed by testUser
        // Share per person for $80 expense is $40
        assertEquals("Total Owed: $40.00", totalOwedLabel.getText());
    }

    @Test
    @DisplayName("Test expense load failure")
    void testExpenseLoadFailure(FxRobot robot) {
        // Mock null return for loadGroupExpenses
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(null);
        
        // Trigger reload on JavaFX thread
        Platform.runLater(() -> {
            controller.reloadExpenses();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        TableView<Expense> expenseTable = robot.lookup("#expenseTableView").queryAs(TableView.class);
        Label totalOwedLabel = robot.lookup("#totalOwedLabel").queryAs(Label.class);
        
        // Verify table is empty
        assertEquals(0, expenseTable.getItems().size());
        // Verify total owed is zero
        assertEquals("Total Owed: $0.00", totalOwedLabel.getText());
    }

    @Test
    @DisplayName("Test status column display")
    void testStatusColumnDisplay(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify status text for different expense types
        assertTrue(robot.lookup("You paid").tryQuery().isPresent()); // For expense paid by user
        assertTrue(robot.lookup("Settled").tryQuery().isPresent()); // For settled expense
        assertTrue(robot.lookup("Owe: $40.00").tryQuery().isPresent()); // For unsettled expense
    }

    @Test
    @DisplayName("Test add new expense button opens modal")
    void testAddNewExpenseButton(FxRobot robot) {
        // Click add expense button
        robot.clickOn("#addExpenseButton");
        
        // Wait for modal to open
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify new window is opened
        assertTrue(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }
}