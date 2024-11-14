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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.Expense;

@Tag("service")
class ExpenseServiceTest {

    private ExpenseService expenseService;
    private String originalGroupExpensePath;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    @DisplayName("Set up temporary directory and initialize ExpenseService")
    private void setUp() {
        expenseService = new ExpenseService();
        originalGroupExpensePath = (String) ReflectionTestUtils.getField(expenseService, "groupExpensePath");
        ReflectionTestUtils.setField(expenseService, "groupExpensePath", tempDir.toString() + File.separator);
    }

    @AfterEach
    @DisplayName("Restore original path after tests")
    public void tearDown() throws IOException {
        ReflectionTestUtils.setField(expenseService, "groupExpensePath", originalGroupExpensePath);
    }

    @Test
    @DisplayName("Test loading group expenses when file does not exist returns empty list")
    public void loadGroupExpenses_WhenFileDoesNotExist_ReturnsEmptyList() {
        String groupName = "nonexistentGroup";
        List<Expense> result = expenseService.loadGroupExpenses(groupName);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test loading group expenses when file exists returns expense list")
    public void loadGroupExpenses_WhenFileExists_ReturnsExpenseList() throws IOException {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> expectedExpenses = Arrays.asList(
            createTestExpense("Test Expense 1", 100.0, "user1", participants),
            createTestExpense("Test Expense 2", 200.0, "user2", participants)
        );

        ObjectMapper mapper = new ObjectMapper();
        File testFile = new File(tempDir.toString(), groupName + ".json");
        mapper.writeValue(testFile, expectedExpenses);

        List<Expense> result = expenseService.loadGroupExpenses(groupName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Expense 1", result.get(0).getDescription());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals("Test Expense 2", result.get(1).getDescription());
        assertEquals(200.0, result.get(1).getAmount());
    }

    @Test
    @DisplayName("Test successful saving of group expenses")
    public void updateGroupExpenses_Successfully_SavesExpenses() throws IOException {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> expenses = Arrays.asList(createTestExpense("Test Expense", 150.0, "user1", participants));

        expenseService.updateGroupExpenses(groupName, expenses);

        File savedFile = new File(tempDir.toString(), groupName + ".json");
        assertTrue(savedFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        List<Expense> savedExpenses = mapper.readValue(savedFile, 
            mapper.getTypeFactory().constructCollectionType(List.class, Expense.class));
        assertEquals(1, savedExpenses.size());
        assertEquals("Test Expense", savedExpenses.get(0).getDescription());
    }

    @Test
    @DisplayName("Test update and load group expenses integration")
    public void updateGroupExpenses_AndLoadGroupExpenses_WorksTogether() {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        List<Expense> originalExpenses = Arrays.asList(createTestExpense("Test Expense", 150.0, "user1", participants));

        expenseService.updateGroupExpenses(groupName, originalExpenses);
        List<Expense> loadedExpenses = expenseService.loadGroupExpenses(groupName);

        assertNotNull(loadedExpenses);
        assertEquals(1, loadedExpenses.size());
        assertEquals("Test Expense", loadedExpenses.get(0).getDescription());
        assertEquals(150.0, loadedExpenses.get(0).getAmount());
        assertEquals("user1", loadedExpenses.get(0).getPaidBy());
    }

    @Test
    @DisplayName("Test update group expenses with null group name throws exception")
    public void updateGroupExpenses_WithNullGroup_ThrowsException() {
        String groupName = null;
        List<Expense> expenses = new ArrayList<>();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.updateGroupExpenses(groupName, expenses));
        assertEquals("Group name cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test update group expenses with null expenses list throws exception")
    public void updateGroupExpenses_WithNullExpenses_ThrowsException() {
        String groupName = "testGroup";
        List<Expense> expenses = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.updateGroupExpenses(groupName, expenses));
        assertEquals("Expenses list cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test load group expenses with null group name throws exception")
    public void loadGroupExpenses_WithNullGroup_ThrowsException() {
        String groupName = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> expenseService.loadGroupExpenses(groupName));
        assertEquals("Group name cannot be null", exception.getMessage());
    }

    private Expense createTestExpense(String description, double amount, String paidBy, List<String> participants) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setPaidBy(paidBy);
        expense.setParticipants(new ArrayList<>(participants));
        return expense;
    }
}