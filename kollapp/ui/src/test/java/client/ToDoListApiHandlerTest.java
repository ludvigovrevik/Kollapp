package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import client.ToDoListApiHandler;
import core.ToDoList;
import core.User;
import core.UserGroup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

/**
 * Unit tests for the {@link ToDoListApiHandler} class.
 */
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
    @DisplayName("Set up mocks and initialize ToDoListApiHandler")
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        testUser = new User();
        testUser.setUsername("testUser");
        testGroup = new UserGroup("testGroup");
        testToDoList = new ToDoList();
        
        toDoListApiHandler = new ToDoListApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    @DisplayName("Load to-do list - Success scenario")
    @Tag("loadToDoList")
    public void loadToDoList_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(testToDoList);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isPresent());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Load to-do list - Failure scenario")
    @Tag("loadToDoList")
    public void loadToDoList_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Load to-do list - Exception handling")
    @Tag("loadToDoList")
    public void loadToDoList_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        Optional<ToDoList> result = toDoListApiHandler.loadToDoList(testUser);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign to-do list - Success scenario")
    @Tag("assignToDoList")
    public void assignToDoList_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.assignToDoList(testUser);

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign to-do list - Failure scenario")
    @Tag("assignToDoList")
    public void assignToDoList_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.assignToDoList(testUser);

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Update to-do list - Success scenario")
    @Tag("updateToDoList")
    public void updateToDoList_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.updateToDoList(testUser, testToDoList);

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Update to-do list - Null user")
    @Tag("updateToDoList")
    public void updateToDoList_NullUser() throws IOException, InterruptedException {
        boolean result = toDoListApiHandler.updateToDoList(null, testToDoList);

        assertFalse(result);
        verify(mockHttpClient, never()).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Update to-do list - Empty username")
    @Tag("updateToDoList")
    public void updateToDoList_EmptyUsername() throws IOException, InterruptedException {
        User userWithEmptyUsername = new User();

        boolean result = toDoListApiHandler.updateToDoList(userWithEmptyUsername, testToDoList);

        assertFalse(result);
        verify(mockHttpClient, never()).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Update to-do list - Failure scenario")
    @Tag("updateToDoList")
    public void updateToDoList_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.updateToDoList(testUser, testToDoList);

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Load group to-do list - Success scenario")
    @Tag("loadGroupToDoList")
    public void loadGroupToDoList_Success() throws IOException, InterruptedException {
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
    @DisplayName("Load group to-do list - Failure scenario")
    @Tag("loadGroupToDoList")
    public void loadGroupToDoList_Failure() throws IOException, InterruptedException {
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
    @DisplayName("Load group to-do list - Exception handling")
    @Tag("loadGroupToDoList")
    public void loadGroupToDoList_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        Optional<ToDoList> result = toDoListApiHandler.loadGroupToDoList(testGroup);

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Update group to-do list - Success scenario")
    @Tag("updateGroupToDoList")
    public void updateGroupToDoList_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Update group to-do list - Failure scenario")
    @Tag("updateGroupToDoList")
    public void updateGroupToDoList_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Update group to-do list - Exception handling")
    @Tag("updateGroupToDoList")
    public void updateGroupToDoList_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        boolean result = toDoListApiHandler.updateGroupToDoList(testGroup, testToDoList);

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}