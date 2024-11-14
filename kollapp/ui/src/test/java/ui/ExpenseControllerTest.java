package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import client.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link ExpenseController} class.
 */
@ExtendWith(ApplicationExtension.class)
public class ExpenseControllerTest {
    
    @Mock
    private ExpenseApiHandler mockExpenseHandler;
    private User testUser;
    private UserGroup testGroup;
    private ExpenseController controller;
    private List<Expense> testExpenses;
    private Stage stage;

    // Headless mode configuration
    private static final boolean headless = true;

    @BeforeAll
    private static void setupHeadlessMode() {
        if (headless) {
            System.setProperty("testfx.headless", "true");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    @Start
    private void start(Stage stage) throws Exception {
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
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(Optional.of(testExpenses));
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
    private void setUp() {
        // Reset mocks and set up default behavior
        reset(mockExpenseHandler);
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(Optional.of(testExpenses));
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);
    }

    @Test
    @DisplayName("Test expense table initialization")
    @Tag("expense")
    public void testExpenseTableInitialization(FxRobot robot) {
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
    @Tag("expense")
    public void testTotalOwedCalculation(FxRobot robot) {
        Label totalOwedLabel = robot.lookup("#totalOwedLabel").queryAs(Label.class);
        
        // Only expense2 (Groceries) is unsettled and owed by testUser
        // Share per person for $80 expense is $40
        assertEquals("Total Owed: $40.00", totalOwedLabel.getText());
    }

    @Test
    @DisplayName("Test expense load failure")
    void testExpenseLoadFailure(FxRobot robot) {
        // Mock null return for loadGroupExpenses
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(Optional.empty());
        
        // Trigger reload on JavaFX thread
        Platform.runLater(() -> {
            controller.reloadExpenses();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        TableView<Expense> expenseTable = robot.lookup("#expenseTableView").queryAs(TableView.class);
        Label totalOwedLabel = robot.lookup("#totalOwedLabel").queryAs(Label.class);
        
        assertEquals(0, expenseTable.getItems().size());
        assertEquals("Total Owed: $0.00", totalOwedLabel.getText());
    }

    @Test
    @DisplayName("Test status column display")
    @Tag("expense")
    public void testStatusColumnDisplay(FxRobot robot) {
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify status text for different expense types
        assertTrue(robot.lookup("You paid").tryQuery().isPresent()); // For expense paid by user
        assertTrue(robot.lookup("Settled").tryQuery().isPresent()); // For settled expense
        assertTrue(robot.lookup("Owe: $40.00").tryQuery().isPresent()); // For unsettled expense
    }

    @Test
    @DisplayName("Test add new expense button opens modal")
    @Tag("expense")
    public void testAddNewExpenseButton(FxRobot robot) {
        robot.clickOn("#addExpenseButton");
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }
}