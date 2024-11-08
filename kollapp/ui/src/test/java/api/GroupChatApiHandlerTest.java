package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.GroupChat;
import core.Message;
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

class GroupChatApiHandlerTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockStringResponse;

    @Mock
    private HttpResponse<Void> mockVoidResponse;

    private GroupChatApiHandler groupChatApiHandler;
    private ObjectMapper objectMapper;
    private GroupChat testGroupChat;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize ObjectMapper with JavaTimeModule
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create test data
        testGroupChat = new GroupChat();
        testMessage = new Message("testUser", "Test message");
        testGroupChat.addMessage(testMessage);
        
        // Create a subclass of GroupChatApiHandler to inject mocked HttpClient
        groupChatApiHandler = new GroupChatApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    void createGroupChat_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockStringResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void createGroupChat_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockStringResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void createGroupChat_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getGroupChat_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = objectMapper.writeValueAsString(testGroupChat);
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        Optional<GroupChat> result = groupChatApiHandler.getGroupChat("TestGroup");

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().getMessages().isEmpty());
        assertEquals(testMessage.getText(), result.get().getMessages().get(0).getText());
        assertEquals(testMessage.getAuthor(), result.get().getMessages().get(0).getAuthor());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getGroupChat_NotFound() throws IOException, InterruptedException {
        // Arrange
        when(mockStringResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        Optional<GroupChat> result = groupChatApiHandler.getGroupChat("NonExistentGroup");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void sendMessage_Success() throws IOException, InterruptedException {
        // Arrange
        Message message = new Message("testUser", "Test message");
        when(mockStringResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        boolean result = groupChatApiHandler.sendMessage("TestGroup", message);

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void sendMessage_Failure() throws IOException, InterruptedException {
        // Arrange
        Message message = new Message("testUser", "Test message");
        when(mockStringResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        boolean result = groupChatApiHandler.sendMessage("TestGroup", message);

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getMessages_Success() throws IOException, InterruptedException {
        // Arrange
        List<Message> messages = Arrays.asList(testMessage);
        String jsonResponse = objectMapper.writeValueAsString(messages);
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        Optional<List<Message>> result = groupChatApiHandler.getMessages("TestGroup");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testMessage.getText(), result.get().get(0).getText());
        assertEquals(testMessage.getAuthor(), result.get().get(0).getAuthor());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void getMessages_NotFound() throws IOException, InterruptedException {
        // Arrange
        when(mockStringResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        // Act
        Optional<List<Message>> result = groupChatApiHandler.getMessages("NonExistentGroup");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void groupChatExists_True() throws IOException, InterruptedException {
        // Arrange
        when(mockVoidResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenReturn(mockVoidResponse);

        // Act
        boolean result = groupChatApiHandler.groupChatExists("TestGroup");

        // Assert
        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    void groupChatExists_False() throws IOException, InterruptedException {
        // Arrange
        when(mockVoidResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenReturn(mockVoidResponse);

        // Act
        boolean result = groupChatApiHandler.groupChatExists("NonExistentGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    void groupChatExists_Exception() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenThrow(new IOException("Network error"));

        // Act
        boolean result = groupChatApiHandler.groupChatExists("TestGroup");

        // Assert
        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }
}