package api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import api.service.GroupChatService;
import core.GroupChat;
import core.Message;

public class GroupChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GroupChatService groupChatService;

    @InjectMocks
    private GroupChatController groupChatController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupChatController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void createGroupChat_Success() throws Exception {
        String groupName = "testGroup";

        mockMvc.perform(post("/api/v1/groupchats/{groupName}", groupName))
                .andExpect(status().isOk())
                .andExpect(content().string("Group chat created successfully."));
    }

    @Test
    public void createGroupChat_Failure() throws Exception {
        String groupName = "testGroup";
        doThrow(new IllegalArgumentException("Group already exists"))
                .when(groupChatService).createGroupChat(groupName);

        mockMvc.perform(post("/api/v1/groupchats/{groupName}", groupName))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Group already exists"));
    }

    @Test
    public void getGroupChat_Success() throws Exception {
        String groupName = "testGroup";
        GroupChat groupChat = new GroupChat();
        Message message = new Message();
        message.setText("Hello");
        message.setAuthor("user1");
        groupChat.addMessage(message);
        
        when(groupChatService.getGroupChat(groupName)).thenReturn(groupChat);

        mockMvc.perform(get("/api/v1/groupchats/{groupName}", groupName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").exists())
                .andExpect(jsonPath("$.messages[0].author").value("user1"))
                .andExpect(jsonPath("$.messages[0].text").value("Hello"));
    }

    @Test
    public void getGroupChat_NotFound() throws Exception {
        String groupName = "nonExistentGroup";
        when(groupChatService.getGroupChat(groupName))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(get("/api/v1/groupchats/{groupName}", groupName))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sendMessage_Success() throws Exception {
        String groupName = "testGroup";
        Message message = new Message();
        message.setText("Hello, world!");
        message.setAuthor("user1");

        mockMvc.perform(post("/api/v1/groupchats/{groupName}/messages", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent successfully."));
    }

    @Test
    public void sendMessage_Failure() throws Exception {
        String groupName = "testGroup";
        Message message = new Message();
        message.setText("Hello, world!");
        message.setAuthor("user1");

        doThrow(new IllegalArgumentException("Group not found"))
                .when(groupChatService).sendMessage(anyString(), any(Message.class));

        mockMvc.perform(post("/api/v1/groupchats/{groupName}/messages", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Group not found"));
    }

    @Test
    public void getMessages_Success() throws Exception {
        String groupName = "testGroup";
        Message message1 = new Message();
        message1.setText("Hello");
        message1.setAuthor("user1");
        
        Message message2 = new Message();
        message2.setText("Hi there");
        message2.setAuthor("user2");
        
        List<Message> messages = Arrays.asList(message1, message2);
        when(groupChatService.getMessages(groupName)).thenReturn(messages);

        mockMvc.perform(get("/api/v1/groupchats/{groupName}/messages", groupName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("user1"))
                .andExpect(jsonPath("$[0].text").value("Hello"))
                .andExpect(jsonPath("$[1].author").value("user2"))
                .andExpect(jsonPath("$[1].text").value("Hi there"));
    }

    @Test
    public void getMessages_NotFound() throws Exception {
        String groupName = "nonExistentGroup";
        when(groupChatService.getMessages(groupName))
                .thenThrow(new IllegalArgumentException("Group not found"));

        mockMvc.perform(get("/api/v1/groupchats/{groupName}/messages", groupName))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sendMessage_NullText() throws Exception {
        String groupName = "testGroup";
        Message message = new Message();
        message.setAuthor("user1");  // Author is required but text is null

        mockMvc.perform(post("/api/v1/groupchats/{groupName}/messages", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isBadRequest());
    }
}