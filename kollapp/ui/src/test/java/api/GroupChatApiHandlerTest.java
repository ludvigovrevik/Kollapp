package api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import api.GroupChatApiHandler;
import core.GroupChat;
import core.Message;

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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link GroupChatApiHandler} class.
 */
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
    @DisplayName("Set up mocks and initialize GroupChatApiHandler")
    private void setUp() {
        MockitoAnnotations.openMocks(this);
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        testGroupChat = new GroupChat();
        testMessage = new Message("testUser", "Test message");
        testGroupChat.addMessage(testMessage);
        
        groupChatApiHandler = new GroupChatApiHandler() {
            @Override
            protected HttpClient createHttpClient() {
                return mockHttpClient;
            }
        };
    }

    @Test
    @DisplayName("Create group chat - Success scenario")
    @Tag("createGroupChat")
    public void createGroupChat_Success() throws IOException, InterruptedException {
        when(mockStringResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Create group chat - Failure scenario")
    @Tag("createGroupChat")
    public void createGroupChat_Failure() throws IOException, InterruptedException {
        when(mockStringResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Create group chat - Exception handling")
    @Tag("createGroupChat")
    public void createGroupChat_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        boolean result = groupChatApiHandler.createGroupChat("TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get group chat - Success scenario")
    @Tag("getGroupChat")
    public void getGroupChat_Success() throws IOException, InterruptedException {
        String jsonResponse = objectMapper.writeValueAsString(testGroupChat);
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        Optional<GroupChat> result = groupChatApiHandler.getGroupChat("TestGroup");

        assertTrue(result.isPresent());
        assertFalse(result.get().getMessages().isEmpty());
        assertEquals(testMessage.getText(), result.get().getMessages().get(0).getText());
        assertEquals(testMessage.getAuthor(), result.get().getMessages().get(0).getAuthor());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get group chat - Not Found")
    @Tag("getGroupChat")
    public void getGroupChat_NotFound() throws IOException, InterruptedException {
        when(mockStringResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        Optional<GroupChat> result = groupChatApiHandler.getGroupChat("NonExistentGroup");

        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Send message - Success scenario")
    @Tag("sendMessage")
    public void sendMessage_Success() throws IOException, InterruptedException {
        Message message = new Message("testUser", "Test message");
        when(mockStringResponse.statusCode()).thenReturn(201);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        boolean result = groupChatApiHandler.sendMessage("TestGroup", message);

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Send message - Failure scenario")
    @Tag("sendMessage")
    public void sendMessage_Failure() throws IOException, InterruptedException {
        Message message = new Message("testUser", "Test message");
        when(mockStringResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        boolean result = groupChatApiHandler.sendMessage("TestGroup", message);

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get messages - Success scenario")
    @Tag("getMessages")
    public void getMessages_Success() throws IOException, InterruptedException {
        List<Message> messages = Arrays.asList(testMessage);
        String jsonResponse = objectMapper.writeValueAsString(messages);
        when(mockStringResponse.statusCode()).thenReturn(200);
        when(mockStringResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        Optional<List<Message>> result = groupChatApiHandler.getMessages("TestGroup");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(testMessage.getText(), result.get().get(0).getText());
        assertEquals(testMessage.getAuthor(), result.get().get(0).getAuthor());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Get messages - Not Found")
    @Tag("getMessages")
    public void getMessages_NotFound() throws IOException, InterruptedException {
        when(mockStringResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockStringResponse);

        Optional<List<Message>> result = groupChatApiHandler.getMessages("NonExistentGroup");

        assertTrue(result.isEmpty());
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("Group chat exists - True case")
    @Tag("groupChatExists")
    public void groupChatExists_True() throws IOException, InterruptedException {
        when(mockVoidResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenReturn(mockVoidResponse);

        boolean result = groupChatApiHandler.groupChatExists("TestGroup");

        assertTrue(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    @DisplayName("Group chat exists - False case")
    @Tag("groupChatExists")
    public void groupChatExists_False() throws IOException, InterruptedException {
        when(mockVoidResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenReturn(mockVoidResponse);

        boolean result = groupChatApiHandler.groupChatExists("NonExistentGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    @DisplayName("Group chat exists - Exception handling")
    @Tag("groupChatExists")
    public void groupChatExists_Exception() throws IOException, InterruptedException {
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
            .thenThrow(new IOException("Network error"));

        boolean result = groupChatApiHandler.groupChatExists("TestGroup");

        assertFalse(result);
        verify(mockHttpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }
}