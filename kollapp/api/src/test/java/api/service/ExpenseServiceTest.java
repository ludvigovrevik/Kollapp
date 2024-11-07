package api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.Expense;

class ExpenseServiceTest {

    private ExpenseService expenseService;
    private String originalGroupExpensePath;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService();
        // Store the original path
        originalGroupExpensePath = (String) ReflectionTestUtils.getField(expenseService, "groupExpensePath");
        // Set the temporary directory for testing
        ReflectionTestUtils.setField(expenseService, "groupExpensePath", tempDir.toString() + File.separator);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restore the original path
        ReflectionTestUtils.setField(expenseService, "groupExpensePath", originalGroupExpensePath);
    }

    @Test
    void loadGroupExpenses_WhenFileDoesNotExist_ReturnsEmptyList() {
        // Arrange
        String groupName = "nonexistentGroup";

        // Act
        List<Expense> result = expenseService.loadGroupExpenses(groupName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void loadGroupExpenses_WhenFileExists_ReturnsExpenseList() throws IOException {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> expectedExpenses = Arrays.asList(
            createTestExpense("Test Expense 1", 100.0, "user1", participants),
            createTestExpense("Test Expense 2", 200.0, "user2", participants)
        );

        // Create and write to test file
        ObjectMapper mapper = new ObjectMapper();
        File testFile = new File(tempDir.toString(), groupName + ".json");
        mapper.writeValue(testFile, expectedExpenses);

        // Act
        List<Expense> result = expenseService.loadGroupExpenses(groupName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Expense 1", result.get(0).getDescription());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals("Test Expense 2", result.get(1).getDescription());
        assertEquals(200.0, result.get(1).getAmount());
    }

    @Test
    void updateGroupExpenses_Successfully_SavesExpenses() throws IOException {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> expenses = Arrays.asList(
            createTestExpense("Test Expense", 150.0, "user1", participants)
        );

        // Act
        expenseService.updateGroupExpenses(groupName, expenses);

        // Assert
        File savedFile = new File(tempDir.toString(), groupName + ".json");
        assertTrue(savedFile.exists());
        
        // Verify content
        ObjectMapper mapper = new ObjectMapper();
        List<Expense> savedExpenses = mapper.readValue(savedFile, 
            mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        assertEquals(1, savedExpenses.size());
        assertEquals("Test Expense", savedExpenses.get(0).getDescription());
    }

    @Test
    void updateGroupExpenses_AndLoadGroupExpenses_WorksTogether() {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> originalExpenses = Arrays.asList(
            createTestExpense("Test Expense", 150.0, "user1", participants)
        );

        // Act
        expenseService.updateGroupExpenses(groupName, originalExpenses);
        List<Expense> loadedExpenses = expenseService.loadGroupExpenses(groupName);

        // Assert
        assertNotNull(loadedExpenses);
        assertEquals(1, loadedExpenses.size());
        assertEquals("Test Expense", loadedExpenses.get(0).getDescription());
        assertEquals(150.0, loadedExpenses.get(0).getAmount());
        assertEquals("user1", loadedExpenses.get(0).getPaidBy());
    }

    @Test
    void updateGroupExpenses_WithNullGroup_ThrowsException() {
        // Arrange
        String groupName = null;
        List<Expense> expenses = new ArrayList<>();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.updateGroupExpenses(groupName, expenses));
        assertEquals("Group name cannot be null", exception.getMessage());
    }

    @Test
    void updateGroupExpenses_WithNullExpenses_ThrowsException() {
        // Arrange
        String groupName = "testGroup";
        List<Expense> expenses = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.updateGroupExpenses(groupName, expenses));
        assertEquals("Expenses list cannot be null", exception.getMessage());
    }

    @Test
    void loadGroupExpenses_WithNullGroup_ThrowsException() {
        // Arrange
        String groupName = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.loadGroupExpenses(groupName));
        assertEquals("Group name cannot be null", exception.getMessage());
    }

    @Test
    void updateGroupExpenses_WithInvalidPath_ThrowsException() {
        // Arrange
        String invalidPath = "/invalid/path"; // Invalid path
        ReflectionTestUtils.setField(expenseService, "groupExpensePath", invalidPath);
        
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> expenses = Arrays.asList(
            createTestExpense("Test Expense", 150.0, "user1", participants)
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> expenseService.updateGroupExpenses(groupName, expenses));
    }

    // Helper method to create test expenses
    private Expense createTestExpense(String description, double amount, String paidBy, List<String> participants) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setPaidBy(paidBy);
        expense.setParticipants(new ArrayList<>(participants));
        return expense;
    }
}
