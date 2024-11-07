package api.controller;

import api.service.ToDoListService;
import core.ToDoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ToDoListControllerTest {

    @Mock
    private ToDoListService toDoListService;

    @InjectMocks
    private ToDoListController toDoListController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(toDoListController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void loadToDoList_Success() throws Exception {
        // Arrange
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        when(toDoListService.loadToDoList(anyString())).thenReturn(toDoList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/todolists/{username}", username))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(toDoListService).loadToDoList(username);
    }

    @Test
    void loadToDoList_NotFound() throws Exception {
        // Arrange
        String username = "testUser";
        when(toDoListService.loadToDoList(anyString()))
            .thenThrow(new IllegalArgumentException());

        // Act & Assert
        mockMvc.perform(get("/api/v1/todolists/{username}", username))
            .andExpect(status().isNotFound());

        verify(toDoListService).loadToDoList(username);
    }

    @Test
    void assignToDoList_Success() throws Exception {
        // Arrange
        String username = "testUser";
        doNothing().when(toDoListService).assignToDoList(anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/todolists/{username}", username))
            .andExpect(status().isCreated());

        verify(toDoListService).assignToDoList(username);
    }

    @Test
    void assignToDoList_BadRequest() throws Exception {
        // Arrange
        String username = "testUser";
        doThrow(new IllegalArgumentException())
            .when(toDoListService).assignToDoList(anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/todolists/{username}", username))
            .andExpect(status().isBadRequest());

        verify(toDoListService).assignToDoList(username);
    }

    @Test
    void updateToDoList_Success() throws Exception {
        // Arrange
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        doNothing().when(toDoListService).updateToDoList(anyString(), any(ToDoList.class));

        // Act & Assert
        mockMvc.perform(put("/api/v1/todolists/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isOk());

        verify(toDoListService).updateToDoList(eq(username), any(ToDoList.class));
    }

    @Test
    void updateToDoList_BadRequest() throws Exception {
        // Arrange
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        doThrow(new IllegalArgumentException())
            .when(toDoListService).updateToDoList(anyString(), any(ToDoList.class));

        // Act & Assert
        mockMvc.perform(put("/api/v1/todolists/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isBadRequest());

        verify(toDoListService).updateToDoList(eq(username), any(ToDoList.class));
    }

    @Test
    void loadGroupToDoList_Success() throws Exception {
        // Arrange
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        when(toDoListService.loadGroupToDoList(anyString())).thenReturn(toDoList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/todolists/groups/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(toDoListService).loadGroupToDoList(groupName);
    }

    @Test
    void loadGroupToDoList_NotFound() throws Exception {
        // Arrange
        String groupName = "testGroup";
        when(toDoListService.loadGroupToDoList(anyString()))
            .thenThrow(new IllegalArgumentException());

        // Act & Assert
        mockMvc.perform(get("/api/v1/todolists/groups/{groupName}", groupName))
            .andExpect(status().isNotFound());

        verify(toDoListService).loadGroupToDoList(groupName);
    }

    @Test
    void updateGroupToDoList_Success() throws Exception {
        // Arrange
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        doNothing().when(toDoListService).updateGroupToDoList(anyString(), any(ToDoList.class));

        // Act & Assert
        mockMvc.perform(put("/api/v1/todolists/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isOk());

        verify(toDoListService).updateGroupToDoList(eq(groupName), any(ToDoList.class));
    }

    @Test
    void updateGroupToDoList_BadRequest() throws Exception {
        // Arrange
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        doThrow(new IllegalArgumentException())
            .when(toDoListService).updateGroupToDoList(anyString(), any(ToDoList.class));

        // Act & Assert
        mockMvc.perform(put("/api/v1/todolists/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isBadRequest());

        verify(toDoListService).updateGroupToDoList(eq(groupName), any(ToDoList.class));
    }
}