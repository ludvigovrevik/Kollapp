package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
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

import api.ExpenseApiHandler;
import core.Expense;
import core.User;
import core.UserGroup;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Unit tests for the {@link AddNewExpenseController} class.
 */
@ExtendWith(ApplicationExtension.class)
public class AddNewExpenseControllerTest {

    @Mock
    private ExpenseApiHandler mockExpenseHandler;
    private User testUser;
    private UserGroup testGroup;
    private AddNewExpenseController controller;

    // Headless mode configuration
    private static boolean headless = true;

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
    public void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddNewExpense.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setExpenseApiHandler(mockExpenseHandler);

        testUser = new User("testUser", "password");
        testGroup = new UserGroup("testGroup");
        testGroup.addUser("testUser");
        testGroup.addUser("otherUser");

        controller.initializeController(testUser, testGroup);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    private void setUp() {
        reset(mockExpenseHandler);
    }

    @Test
    @DisplayName("Test successful expense addition")
    @Tag("expense")
    public void testSuccessfulExpenseAddition(FxRobot robot) {
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(any(UserGroup.class))).thenReturn(Optional.of(existingExpenses));
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        verify(mockExpenseHandler).loadGroupExpenses(any(UserGroup.class));
        verify(mockExpenseHandler).updateGroupExpenses(any(UserGroup.class), any());
    }

    @Test
    @DisplayName("Test empty fields validation")
    @Tag("expense")
    public void testEmptyFieldsValidation(FxRobot robot) {
        robot.clickOn("#addExpenseButton");

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Please fill in all fields.", errorLabel.getText());
    }

    @Test
    @DisplayName("Test invalid amount format")
    @Tag("expense")
    public void testInvalidAmountFormat(FxRobot robot) {
        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("invalid");
        robot.clickOn("#addExpenseButton");

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Amount must be a number.", errorLabel.getText());
    }

    @Test
    @DisplayName("Test expense addition failure")
    @Tag("expense")
    public void testExpenseAdditionFailure(FxRobot robot) {
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(Optional.of(existingExpenses));
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(false);

        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        Label errorLabel = robot.lookup("#errorLabel").queryAs(Label.class);
        assertEquals("Failed to add expense.", errorLabel.getText());
        
        assertTrue(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }

    @Test
    @DisplayName("Test null existing expenses handling")
    @Tag("expense")
    void testNullExistingExpenses(FxRobot robot) {
        // Mock null existing expenses
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(Optional.empty());
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        robot.clickOn("#expenseNameField").write("Dinner");
        robot.clickOn("#amountField").write("50.00");
        robot.clickOn("#addExpenseButton");

        verify(mockExpenseHandler).loadGroupExpenses(testGroup);
        verify(mockExpenseHandler).updateGroupExpenses(eq(testGroup), argThat(list ->
            list != null && list.size() == 1 &&
            list.get(0).getDescription().equals("Dinner") &&
            list.get(0).getAmount() == 50.00 &&
            list.get(0).getPaidBy().equals("testUser") &&
            list.get(0).getParticipants().containsAll(testGroup.getUsers())
        ));

        assertFalse(robot.lookup("#expenseNameField").tryQuery().isPresent());
    }

    @Test
    @DisplayName("Test expense addition with group participants")
    @Tag("expense")
    public void testExpenseAdditionWithParticipants(FxRobot robot) {
        List<Expense> existingExpenses = new ArrayList<>();
        when(mockExpenseHandler.loadGroupExpenses(testGroup)).thenReturn(Optional.of(existingExpenses));
        when(mockExpenseHandler.updateGroupExpenses(any(UserGroup.class), any())).thenReturn(true);

        robot.clickOn("#expenseNameField").write("Group Dinner");
        robot.clickOn("#amountField").write("100.00");
        robot.clickOn("#addExpenseButton");

        verify(mockExpenseHandler).updateGroupExpenses(eq(testGroup), argThat(list -> {
            if (list == null || list.isEmpty()) return false;
            Expense expense = list.get(0);
            return expense.getParticipants().size() == 2 &&
                   expense.getParticipants().containsAll(Arrays.asList("testUser", "otherUser"));
        }));
    }
}