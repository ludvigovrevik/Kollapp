package api.controller;

import api.service.GroupService;
import core.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    void getGroup_WhenExists_ReturnsGroup() throws Exception {
        // Arrange
        String groupName = "testGroup";
        UserGroup userGroup = new UserGroup();
        when(groupService.getGroup(groupName)).thenReturn(Optional.of(userGroup));

        // Act & Assert
        mockMvc.perform(get("/api/v1/groups/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(groupService).getGroup(groupName);
    }

    @Test
    void getGroup_WhenNotExists_ReturnsNotFound() throws Exception {
        // Arrange
        String groupName = "nonExistentGroup";
        when(groupService.getGroup(groupName)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/groups/{groupName}", groupName))
            .andExpect(status().isNotFound());

        verify(groupService).getGroup(groupName);
    }

    @Test
    void createGroup_Success() throws Exception {
        // Arrange
        String username = "testUser";
        String groupName = "testGroup";
        doNothing().when(groupService).createGroup(username, groupName);

        // Act & Assert
        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isCreated());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    void createGroup_BadRequest() throws Exception {
        // Arrange
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new IllegalArgumentException())
            .when(groupService).createGroup(username, groupName);

        // Act & Assert
        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isBadRequest());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    void createGroup_InternalServerError() throws Exception {
        // Arrange
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new RuntimeException())
            .when(groupService).createGroup(username, groupName);

        // Act & Assert
        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isInternalServerError());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    void assignUserToGroup_Success() throws Exception {
        // Arrange
        String groupName = "testGroup";
        String username = "testUser";
        doNothing().when(groupService).assignUserToGroup(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/groups/{groupName}/assignUser", groupName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
            .andExpect(status().isOk());

        verify(groupService).assignUserToGroup(username, groupName);
    }

    @Test
    void assignUserToGroup_BadRequest() throws Exception {
        // Arrange
        String groupName = "testGroup";
        String username = "testUser";
        doThrow(new IllegalArgumentException())
            .when(groupService).assignUserToGroup(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/groups/{groupName}/assignUser", groupName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
            .andExpect(status().isBadRequest());

        verify(groupService).assignUserToGroup(username, groupName);
    }

    @Test
    void groupExists_ReturnsTrue() throws Exception {
        // Arrange
        String groupName = "testGroup";
        when(groupService.groupExists(groupName)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        verify(groupService).groupExists(groupName);
    }

    @Test
    void groupExists_ReturnsFalse() throws Exception {
        // Arrange
        String groupName = "nonExistentGroup";
        when(groupService.groupExists(groupName)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));

        verify(groupService).groupExists(groupName);
    }

    @Test
    void groupExists_ThrowsException() throws Exception {
        // Arrange
        String groupName = "testGroup";
        when(groupService.groupExists(groupName)).thenThrow(new RuntimeException());

        // Act & Assert
        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isInternalServerError());

        verify(groupService).groupExists(groupName);
    }
}