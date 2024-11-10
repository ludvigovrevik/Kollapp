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

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class AddNewExpenseControllerTest {
    
    @Mock
    private ExpenseApiHandler mockExpenseHandler;
    private User testUser;
    private UserGroup testGroup;
    private AddNewExpenseController controller;

    // Headless mode configuration
    static private boolean headless = true;

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
        MockitoAnnotations.openMocks(this);

        // Load the FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddNewExpense.fxml"));
        Parent root = loader.load();
        
        // Get the controller and inject the mock
        controller = loader.getController();
        controller.setExpenseApiHandler(mockExpenseHandler);
        
        // Set up test data
        testUser = new User("testUser", "password");
        testGroup = new UserGroup("testGroup");
        testGroup.addUser("testUser");
        testGroup.addUser("otherUser");
        
        // Initialize the controller
        controller.initializeController(testUser, testGroup);
        
        // Show the scene
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test
        reset(mockExpenseHandler);
    }

    @Test
    @DisplayName("Test successful expense addition")
    @Tag("expense")
    void testSuccessfulExpenseAddition(FxRobot robot) {
        // Mock successful expense update
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(existingExpenses);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        // Fill in expense details
        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        // Verify interactions
        verify(mockExpenseHandler).loadGroupExpenses(any(UserGroup.class));
        verify(mockExpenseHandler).updateGroupExpenses(any(UserGroup.class), any());
    }

    @Test
    @DisplayName("Test empty fields validation")
    @Tag("expense")
    void testEmptyFieldsValidation(FxRobot robot) {
        // Try to add expense with empty fields
        robot.clickOn("#addExpenseButton");

        // Verify error message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Please fill in all fields.", errorLabel.getText());
    }

    @Test
    @DisplayName("Test invalid amount format")
    @Tag("expense")
    void testInvalidAmountFormat(FxRobot robot) {
        // Fill in expense with invalid amount
        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("invalid");
        robot.clickOn("#addExpenseButton");

        // Verify error message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Amount must be a number.", errorLabel.getText());
    }

    @Test
    @DisplayName("Test expense addition failure")
    @Tag("expense")
    void testExpenseAdditionFailure(FxRobot robot) {
        // Mock failed expense update
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(existingExpenses);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(false);

        // Fill in expense details
        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        // Verify error message
        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Failed to add expense.", errorLabel.getText());
        
        // Verify the window remains open
        assertTrue(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }

    @Test
    @DisplayName("Test null existing expenses handling")
    @Tag("expense")
    void testNullExistingExpenses(FxRobot robot) {
        // Mock null existing expenses
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(null);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        // Fill in expense details
        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        // Verify that a new expense list was created and updated
        verify(mockExpenseHandler).loadGroupExpenses(testGroup);
        verify(mockExpenseHandler).updateGroupExpenses(eq(testGroup), argThat(list -> 
            list != null && list.size() == 1 && 
            list.get(0).getDescription().equals("Dinner") && 
            list.get(0).getAmount() == 50.00 &&
            list.get(0).getPaidBy().equals("testUser") &&
            list.get(0).getParticipants().containsAll(testGroup.getUsers())
        ));
        
        // Verify the window is closed
        assertFalse(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }

    @Test
    @DisplayName("Test expense addition with group participants")
    @Tag("expense")
    void testExpenseAdditionWithParticipants(FxRobot robot) {
        // Mock successful expense update
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(existingExpenses);
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        // Fill in expense details
        robot.clickOn("#expenseNameField").write("Group Dinner");
        robot.clickOn("#amountField").write("100.00");
        robot.clickOn("#addExpenseButton");

        // Verify that the expense was created with the correct participants
        verify(mockExpenseHandler).updateGroupExpenses(eq(testGroup), argThat(list -> {
            if (list == null || list.isEmpty()) return false;
            Expense expense = list.get(0);
            return expense.getParticipants().size() == 2 &&
                   expense.getParticipants().containsAll(Arrays.asList("testUser", "otherUser"));
        }));
    }
}