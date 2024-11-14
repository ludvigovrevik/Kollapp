package api.controller;

import api.service.ExpenseService;
import core.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
@Tag("controller")
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("Initialize MockMvc and ObjectMapper before each test")
    private void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test successful retrieval of group expenses")
    @Tag("load-expenses")
    public void loadGroupExpenses_Success() throws Exception {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2", "user3");
        Expense expense1 = new Expense("Test Expense 1", 100.0, "user1", new ArrayList<>(participants));
        Expense expense2 = new Expense("Test Expense 2", 200.0, "user2", new ArrayList<>(participants));
        List<Expense> expenses = Arrays.asList(expense1, expense2);

        when(expenseService.loadGroupExpenses(anyString())).thenReturn(expenses);

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
    @DisplayName("Test retrieval of group expenses when service throws exception")
    @Tag("load-expenses")
    public void loadGroupExpenses_WhenServiceThrowsException() throws Exception {
        String groupName = "testGroup";
        when(expenseService.loadGroupExpenses(anyString()))
            .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(get("/api/v1/expenses/groups/{groupName}", groupName))
            .andExpect(status().isInternalServerError());

        verify(expenseService).loadGroupExpenses(groupName);
    }

    @Test
    @DisplayName("Test successful update of group expenses")
    @Tag("update-expenses")
    public void updateGroupExpenses_Success() throws Exception {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2", "user3");
        Expense expense = new Expense("Test Expense", 100.0, "user1", new ArrayList<>(participants));
        List<Expense> expenses = Arrays.asList(expense);

        doNothing().when(expenseService).updateGroupExpenses(anyString(), anyList());

        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenses)))
            .andExpect(status().isOk());

        verify(expenseService).updateGroupExpenses(eq(groupName), any());
    }

    @Test
    @DisplayName("Test update of group expenses when service throws exception")
    @Tag("update-expenses")
    public void updateGroupExpenses_WhenServiceThrowsException() throws Exception {
        String groupName = "testGroup";
        List<String> participants = Arrays.asList("user1", "user2");
        Expense expense = new Expense("Test Expense", 100.0, "user1", new ArrayList<>(participants));
        List<Expense> expenses = Arrays.asList(expense);

        doThrow(new IllegalArgumentException("Invalid expenses"))
            .when(expenseService).updateGroupExpenses(anyString(), anyList());

        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenses)))
            .andExpect(status().isBadRequest());

        verify(expenseService).updateGroupExpenses(eq(groupName), any());
    }

    @Test
    @DisplayName("Test update of group expenses with invalid JSON")
    @Tag("update-expenses")
    public void updateGroupExpenses_WithInvalidJson() throws Exception {
        String groupName = "testGroup";
        String invalidJson = "invalid json";

        mockMvc.perform(put("/api/v1/expenses/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}