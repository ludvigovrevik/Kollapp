package api;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.ExpenseApiHandler;
import core.Expense;
import core.UserGroup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ExpenseApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ExpenseApiHandler expenseApiHandler;
    private ObjectMapper objectMapper;
    private UserGroup testGroup;
    private List<Expense> testExpenses;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // Create test data
        testGroup = new UserGroup("TestGroup");
        Expense expense1 = new Expense(/* add required parameters */);
        Expense expense2 = new Expense(/* add required parameters */);
        testExpenses = Arrays.asList(expense1, expense2);

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();
        
        // Create a subclass of ExpenseApiHandler to inject mocked HttpClient
        expenseApiHandler = new ExpenseApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    void loadGroupExpenses_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = objectMapper.writeValueAsString(testExpenses);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<List<Expense>> result = expenseApiHandler.loadGroupExpenses(testGroup);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testExpenses.size(), result.get().size());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupExpenses_NotFound() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<List<Expense>> result = expenseApiHandler.loadGroupExpenses(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupExpenses_ServerError() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<List<Expense>> result = expenseApiHandler.loadGroupExpenses(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupExpenses_IOException() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        Optional<List<Expense>> result = expenseApiHandler.loadGroupExpenses(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupExpenses_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = expenseApiHandler.updateGroupExpenses(testGroup, testExpenses);

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupExpenses_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = expenseApiHandler.updateGroupExpenses(testGroup, testExpenses);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupExpenses_IOException() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = expenseApiHandler.updateGroupExpenses(testGroup, testExpenses);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}