package api;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.GroupApiHandler;
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
 * Unit tests for the {@link GroupApiHandler} class.
 */
class GroupApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private GroupApiHandler groupApiHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("Set up mocks and initialize GroupApiHandler")
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        
        groupApiHandler = new GroupApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    @DisplayName("Get group - Success scenario")
    @Tag("getGroup")
    public void getGroup_Success() throws IOException, InterruptedException {
        UserGroup testGroup = new UserGroup("TestGroup");
        String jsonResponse = objectMapper.writeValueAsString(testGroup);
        
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        Optional<UserGroup> result = groupApiHandler.getGroup("TestGroup");

        assertTrue(result.isPresent());
        assertEquals("TestGroup", result.get().getGroupName());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get group - Not Found")
    @Tag("getGroup")
    public void getGroup_NotFound() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        Optional<UserGroup> result = groupApiHandler.getGroup("NonExistentGroup");

        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get group - Exception handling")
    @Tag("getGroup")
    public void getGroup_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        Optional<UserGroup> result = groupApiHandler.getGroup("TestGroup");

        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Create group - Success scenario")
    @Tag("createGroup")
    public void createGroup_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Create group - Failure scenario")
    @Tag("createGroup")
    public void createGroup_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Create group - Exception handling")
    @Tag("createGroup")
    public void createGroup_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        boolean result = groupApiHandler.createGroup("testUser", "TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign user to group - Success scenario")
    @Tag("assignUserToGroup")
    public void assignUserToGroup_Success() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign user to group - Failure scenario")
    @Tag("assignUserToGroup")
    public void assignUserToGroup_Failure() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Assign user to group - Exception handling")
    @Tag("assignUserToGroup")
    public void assignUserToGroup_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        boolean result = groupApiHandler.assignUserToGroup("testUser", "TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Group exists - True case")
    @Tag("groupExists")
    public void groupExists_True() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("true");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.groupExists("TestGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Group exists - False case")
    @Tag("groupExists")
    public void groupExists_False() throws IOException, InterruptedException {
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("false");
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        boolean result = groupApiHandler.groupExists("NonExistentGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Group exists - Exception handling")
    @Tag("groupExists")
    public void groupExists_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        boolean result = groupApiHandler.groupExists("TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }
}
