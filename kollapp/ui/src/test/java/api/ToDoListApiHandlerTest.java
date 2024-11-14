package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.ToDoList;
import core.User;
import core.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ToDoListApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ToDoListApiHandler toDoListApiHandler;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserGroup testGroup;
    private ToDoList testToDoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize ObjectMapper with JavaTimeModule
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create test data
        testUser = new User();
        testUser.setUsername("testUser");
        testGroup = new UserGroup("testGroup");
        testToDoList = new ToDoList();
        
        // Create a subclass of ToDoListApiHandler to inject mocked HttpClient
        toDoListApiHandler = new ToDoListApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    void loadToDoList_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = objectMapper.writeValueAsString(testToDoList);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isPresent());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadToDoList_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadToDoList_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignToDoList_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.assignToDoList(testUser);

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignToDoList_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.assignToDoList(testUser);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateToDoList_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.updateToDoList(testUser, testToDoList);

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateToDoList_NullUser() throws IOException, InterruptedException {
        // Act
        boolean result = toDoListApiHandler.updateToDoList(null, testToDoList);

        // Assert
        assertFalse(result);
        verify(mockHttpClient, never()).send(any(HttpRequest.class), any());
    }

    @Test
    void updateToDoList_EmptyUsername() throws IOException, InterruptedException {
        // Arrange
        User userWithEmptyUsername = new User();

        // Act
        boolean result = toDoListApiHandler.updateToDoList(userWithEmptyUsername, testToDoList);

        // Assert
        assertFalse(result);
        verify(mockHttpClient, never()).send(any(HttpRequest.class), any());
    }

    @Test
    void updateToDoList_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.updateToDoList(testUser, testToDoList);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupToDoList_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = objectMapper.writeValueAsString(testToDoList);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadGroupToDoList(testGroup);

        // Assert
        assertTrue(result.isPresent());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupToDoList_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadGroupToDoList(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadGroupToDoList_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadGroupToDoList(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupToDoList_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupToDoList_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void updateGroupToDoList_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}