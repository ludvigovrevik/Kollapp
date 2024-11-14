package client;

import com.fasterxml.jackson.databind.ObjectMapper;

import client.UserApiHandler;
import core.User;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link UserApiHandler} class.
 */
class UserApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private UserApiHandler userApiHandler;
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeEach
    @DisplayName("Set up mocks and initialize UserApiHandler")
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        testUser = new User();
        testUser.setUsername("testUser");
        
        userApiHandler = new UserApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    @DisplayName("User exists - True case")
    public void userExists_True() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.userExists("testUser");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("User exists - False case")
    public void userExists_False() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.userExists("nonexistentUser");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("User exists - Error case")
    public void userExists_Error() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.userExists("testUser");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Load user - Success case")
    public void loadUser_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(testUser);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        User result = userApiHandler.loadUser("testUser", "password");

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Load user - Failure case")
    public void loadUser_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        User result = userApiHandler.loadUser("testUser", "wrongPassword");

        assertNull(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Confirm new valid user - Success case")
    public void confirmNewValidUser_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.confirmNewValidUser("newUser", "password", "password");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Confirm new valid user - Failure case")
    public void confirmNewValidUser_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.confirmNewValidUser("newUser", "password", "differentPassword");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Save user - Success case")
    public void saveUser_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        userApiHandler.saveUser(testUser);

        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Save user - Failure case")
    public void saveUser_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        userApiHandler.saveUser(testUser);

        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get user validation error message - Success case")
    public void getUserValidationErrorMessage_Success() throws IOException, InterruptedException {
        String expectedMessage = "Validation successful";
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(expectedMessage);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        String result = userApiHandler.getUserValidationErrorMessage("newUser", "password", "password");

        assertEquals(expectedMessage, result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get user validation error message - Error case")
    public void getUserValidationErrorMessage_Error() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        String result = userApiHandler.getUserValidationErrorMessage("newUser", "password", "different");

        assertTrue(result.startsWith("Error:"));
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Remove user - Success case")
    public void removeUser_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.removeUser("testUser");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Remove user - Bad request")
    public void removeUser_BadRequest() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.removeUser("testUser");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Remove user - Server error")
    public void removeUser_ServerError() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.removeUser("testUser");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign group to user - Success case")
    public void assignGroupToUser_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.assignGroupToUser("testUser", "testGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign group to user - Failure case")
    public void assignGroupToUser_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = userApiHandler.assignGroupToUser("testUser", "nonexistentGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}
