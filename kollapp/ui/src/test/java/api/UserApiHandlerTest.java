package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.User;
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

class UserApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private UserApiHandler userApiHandler;
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        testUser = new User();
        testUser.setUsername("testUser");
        
        // Create a subclass of UserApiHandler to inject mocked HttpClient
        userApiHandler = new UserApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    void userExists_True() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.userExists("testUser");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void userExists_False() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.userExists("nonexistentUser");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void userExists_Error() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.userExists("testUser");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadUser_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = objectMapper.writeValueAsString(testUser);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        User result = userApiHandler.loadUser("testUser", "password").get();

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void loadUser_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        Optional<User> result = userApiHandler.loadUser("testUser", "wrongPassword");

        // Assert
        assertEquals(Optional.empty(), result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void confirmNewValidUser_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.confirmNewValidUser("newUser", "password", "password");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void confirmNewValidUser_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.confirmNewValidUser("newUser", "password", "differentPassword");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void saveUser_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        userApiHandler.saveUser(testUser);

        // Assert
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void saveUser_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        userApiHandler.saveUser(testUser);

        // Assert
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getUserValidationErrorMessage_Success() throws IOException, InterruptedException {
        // Arrange
        String expectedMessage = "Validation successful";
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(expectedMessage);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        String result = userApiHandler.getUserValidationErrorMessage("newUser", "password", "password");

        // Assert
        assertEquals(expectedMessage, result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getUserValidationErrorMessage_Error() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        String result = userApiHandler.getUserValidationErrorMessage("newUser", "password", "different");

        // Assert
        assertTrue(result.startsWith("Error:"));
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void removeUser_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.removeUser("testUser");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void removeUser_BadRequest() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.removeUser("testUser");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void removeUser_ServerError() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.removeUser("testUser");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignGroupToUser_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.assignGroupToUser("testUser", "testGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void assignGroupToUser_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        // Act
        boolean result = userApiHandler.assignGroupToUser("testUser", "nonexistentGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}