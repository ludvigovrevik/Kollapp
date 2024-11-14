package api.controller;

import api.service.ToDoListService;
import core.ToDoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
@Tag("controller")
class ToDoListControllerTest {

    @Mock
    private ToDoListService toDoListService;

    @InjectMocks
    private ToDoListController toDoListController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("Initialize MockMvc and ObjectMapper before each test")
    private void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(toDoListController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test successful loading of user's ToDoList")
    @Tag("load-todolist")
    public void loadToDoList_Success() throws Exception {
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        when(toDoListService.loadToDoList(anyString())).thenReturn(toDoList);

        mockMvc.perform(get("/api/v1/todolists/{username}", username))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(toDoListService).loadToDoList(username);
    }

    @Test
    @DisplayName("Test loading ToDoList when user not found")
    @Tag("load-todolist")
    public void loadToDoList_NotFound() throws Exception {
        String username = "testUser";
        when(toDoListService.loadToDoList(anyString())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/v1/todolists/{username}", username))
            .andExpect(status().isNotFound());

        verify(toDoListService).loadToDoList(username);
    }

    @Test
    @DisplayName("Test successful assignment of ToDoList to user")
    @Tag("assign-todolist")
    public void assignToDoList_Success() throws Exception {
        String username = "testUser";
        doNothing().when(toDoListService).assignToDoList(anyString());

        mockMvc.perform(post("/api/v1/todolists/{username}", username))
            .andExpect(status().isCreated());

        verify(toDoListService).assignToDoList(username);
    }

    @Test
    @DisplayName("Test ToDoList assignment with bad request")
    @Tag("assign-todolist")
    public void assignToDoList_BadRequest() throws Exception {
        String username = "testUser";
        doThrow(new IllegalArgumentException()).when(toDoListService).assignToDoList(anyString());

        mockMvc.perform(post("/api/v1/todolists/{username}", username))
            .andExpect(status().isBadRequest());

        verify(toDoListService).assignToDoList(username);
    }

    @Test
    @DisplayName("Test successful update of user's ToDoList")
    @Tag("update-todolist")
    public void updateToDoList_Success() throws Exception {
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        doNothing().when(toDoListService).updateToDoList(anyString(), any(ToDoList.class));

        mockMvc.perform(put("/api/v1/todolists/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isOk());

        verify(toDoListService).updateToDoList(eq(username), any(ToDoList.class));
    }

    @Test
    @DisplayName("Test update of ToDoList with bad request")
    @Tag("update-todolist")
    public void updateToDoList_BadRequest() throws Exception {
        String username = "testUser";
        ToDoList toDoList = new ToDoList();
        doThrow(new IllegalArgumentException()).when(toDoListService).updateToDoList(anyString(), any(ToDoList.class));

        mockMvc.perform(put("/api/v1/todolists/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isBadRequest());

        verify(toDoListService).updateToDoList(eq(username), any(ToDoList.class));
    }

    @Test
    @DisplayName("Test successful loading of group's ToDoList")
    @Tag("load-group-todolist")
    public void loadGroupToDoList_Success() throws Exception {
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        when(toDoListService.loadGroupToDoList(anyString())).thenReturn(toDoList);

        mockMvc.perform(get("/api/v1/todolists/groups/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(toDoListService).loadGroupToDoList(groupName);
    }

    @Test
    @DisplayName("Test loading group's ToDoList when group not found")
    @Tag("load-group-todolist")
    public void loadGroupToDoList_NotFound() throws Exception {
        String groupName = "testGroup";
        when(toDoListService.loadGroupToDoList(anyString())).thenThrow(new IllegalArgumentException());

        mockMvc.perform(get("/api/v1/todolists/groups/{groupName}", groupName))
            .andExpect(status().isNotFound());

        verify(toDoListService).loadGroupToDoList(groupName);
    }

    @Test
    @DisplayName("Test successful update of group's ToDoList")
    @Tag("update-group-todolist")
    public void updateGroupToDoList_Success() throws Exception {
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        doNothing().when(toDoListService).updateGroupToDoList(anyString(), any(ToDoList.class));

        mockMvc.perform(put("/api/v1/todolists/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isOk());

        verify(toDoListService).updateGroupToDoList(eq(groupName), any(ToDoList.class));
    }

    @Test
    @DisplayName("Test update of group's ToDoList with bad request")
    @Tag("update-group-todolist")
    public void updateGroupToDoList_BadRequest() throws Exception {
        String groupName = "testGroup";
        ToDoList toDoList = new ToDoList();
        doThrow(new IllegalArgumentException()).when(toDoListService).updateGroupToDoList(anyString(), any(ToDoList.class));

        mockMvc.perform(put("/api/v1/todolists/groups/{groupName}", groupName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(toDoList)))
            .andExpect(status().isBadRequest());

        verify(toDoListService).updateGroupToDoList(eq(groupName), any(ToDoList.class));
    }
}