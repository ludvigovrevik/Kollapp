package api.controller;

import api.service.ExpenseService;
import core.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void loadGroupExpenses_Success() throws Exception {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2", "user3");
        Expense expense1 = new Expense();
        expense1.setDescription("Test Expense 1");
        expense1.setAmount(100.0);
        expense1.setPaidBy("user1");
        expense1.setParticipants(new ArrayList<>(participants));

        Expense expense2 = new Expense();
        expense2.setDescription("Test Expense 2");
        expense2.setAmount(200.0);
        expense2.setPaidBy("user2");
        expense2.setParticipants(new ArrayList<>(participants));

        List<Expense> expenses = Arrays.asList(expense1, expense2);
        when(expenseService.loadGroupExpenses(anyString())).thenReturn(expenses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/expenses/groups/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].description").value("Test Expense 1"))
            .andExpect(jsonPath("$[0].amount").value(100.0))
            .andExpect(jsonPath("$[0].paidBy").value("user1"))
            .andExpect(jsonPath("$[1].description").value("Test Expense 2"))
            .andExpect(jsonPath("$[1].amount").value(200.0))
            .andExpect(jsonPath("$[1].paidBy").value("user2"));

        verify(expenseService).loadGroupExpenses(groupName);
    }

    @Test
    void loadGroupExpenses_WhenServiceThrowsException() throws Exception {
        // Arrange
        String groupName = "testGroup";
        when(expenseService.loadGroupExpenses(anyString()))
            .thenThrow(new IllegalArgumentException("Group not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/expenses/groups/{groupName}", groupName))
            .andExpect(status().isInternalServerError());

        verify(expenseService).loadGroupExpenses(groupName);
    }

    @Test
    void updateGroupExpenses_Success() throws Exception {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2", "user3");
        Expense expense = new Expense();
        expense.setDescription("Test Expense");
        expense.setAmount(100.0);
        expense.setPaidBy("user1");
        expense.setParticipants(new ArrayList<>(participants));

        List<Expense> expenses = Arrays.asList(expense);

        // Don't verify exact arguments, just verify the method was called
        doNothing().when(expenseService).updateGroupExpenses(anyString(), anyList());

        // Act & Assert
        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenses)))
            .andExpect(status().isOk());

        verify(expenseService).updateGroupExpenses(eq(groupName), any());
    }

    @Test
    void updateGroupExpenses_WhenServiceThrowsException() throws Exception {
        // Arrange
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        Expense expense = new Expense();
        expense.setDescription("Test Expense");
        expense.setAmount(100.0);
        expense.setPaidBy("user1");
        expense.setParticipants(new ArrayList<>(participants));

        List<Expense> expenses = Arrays.asList(expense);

        // Use anyString() and anyList() for more flexible argument matching
        doThrow(new IllegalArgumentException("Invalid expenses"))
            .when(expenseService).updateGroupExpenses(anyString(), anyList());

        // Act & Assert
        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenses)))
            .andExpect(status().isBadRequest());

        verify(expenseService).updateGroupExpenses(eq(groupName), any());
    }

    @Test
    void updateGroupExpenses_WithInvalidJson() throws Exception {
        // Arrange
        String groupName = "testGroup";
        String invalidJson = "invalid json";

        // Act & Assert
        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
