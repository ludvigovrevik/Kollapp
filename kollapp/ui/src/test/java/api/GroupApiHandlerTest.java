package api;

import com.fasterxml.jackson.databind.ObjectMapper;
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

class GroupApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private GroupApiHandler groupApiHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        
        // Create a subclass of GroupApiHandler to inject mocked HttpClient
        groupApiHandler = new GroupApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    void getGroup_Success() throws IOException, InterruptedException {
        // Arrange
        UserGroup testGroup = new UserGroup("TestGroup");
        String jsonResponse = objectMapper.writeValueAsString(testGroup);
        
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<UserGroup> result = groupApiHandler.getGroup("TestGroup");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("TestGroup", result.get().getGroupName());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getGroup_NotFound() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<UserGroup> result = groupApiHandler.getGroup("NonExistentGroup");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getGroup_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        Optional<UserGroup> result = groupApiHandler.getGroup("TestGroup");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void createGroup_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void createGroup_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void createGroup_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignUserToGroup_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignUserToGroup_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignUserToGroup_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void groupExists_True() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.groupExists("TestGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void groupExists_False() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = groupApiHandler.groupExists("NonExistentGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void groupExists_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = groupApiHandler.groupExists("TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}