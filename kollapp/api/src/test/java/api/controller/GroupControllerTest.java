package api.controller;

import api.service.GroupService;
import core.UserGroup;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Tag("controller")
class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    private MockMvc mockMvc;

    @BeforeEach
    @DisplayName("Initialize MockMvc before each test")
    private void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }

    @Test
    @DisplayName("Test GroupCreationRequest DTO getters and setters")
    @Tag("dto")
    public void groupCreationRequest_GettersAndSetters() {
        GroupController.GroupCreationRequest request = new GroupController.GroupCreationRequest();

        String testUsername = "testUser";
        request.setUsername(testUsername);
        assertEquals(testUsername, request.getUsername(), "Username getter/setter should work correctly");

        String testGroupName = "testGroup";
        request.setGroupName(testGroupName);
        assertEquals(testGroupName, request.getGroupName(), "GroupName getter/setter should work correctly");
    }

    @Test
    @DisplayName("Test GroupCreationRequest DTO with null values")
    @Tag("dto")
    public void groupCreationRequest_NullValues() {
        GroupController.GroupCreationRequest request = new GroupController.GroupCreationRequest();

        assertNull(request.getUsername(), "Username should be null initially");
        assertNull(request.getGroupName(), "GroupName should be null initially");

        request.setUsername(null);
        request.setGroupName(null);
        assertNull(request.getUsername(), "Username should allow null value");
        assertNull(request.getGroupName(), "GroupName should allow null value");
    }

    @Test
    @DisplayName("Test GroupCreationRequest DTO with empty values")
    @Tag("dto")
    public void groupCreationRequest_EmptyValues() {
        GroupController.GroupCreationRequest request = new GroupController.GroupCreationRequest();

        String emptyString = "";
        request.setUsername(emptyString);
        request.setGroupName(emptyString);

        assertEquals(emptyString, request.getUsername(), "Username should allow empty string");
        assertEquals(emptyString, request.getGroupName(), "GroupName should allow empty string");
    }

    @Test
    @DisplayName("Test getting group when it exists")
    @Tag("get-group")
    public void getGroup_WhenExists_ReturnsGroup() throws Exception {
        String groupName = "testGroup";
        UserGroup userGroup = new UserGroup();
        when(groupService.getGroup(groupName)).thenReturn(Optional.of(userGroup));

        mockMvc.perform(get("/api/v1/groups/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(groupService).getGroup(groupName);
    }

    @Test
    @DisplayName("Test getting group when it does not exist")
    @Tag("get-group")
    public void getGroup_WhenNotExists_ReturnsNotFound() throws Exception {
        String groupName = "nonExistentGroup";
        when(groupService.getGroup(groupName)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/groups/{groupName}", groupName))
            .andExpect(status().isNotFound());

        verify(groupService).getGroup(groupName);
    }

    @Test
    @DisplayName("Test successful group creation")
    @Tag("create-group")
    public void createGroup_Success() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doNothing().when(groupService).createGroup(username, groupName);

        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isCreated());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    @DisplayName("Test group creation with bad request")
    @Tag("create-group")
    public void createGroup_BadRequest() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new IllegalArgumentException()).when(groupService).createGroup(username, groupName);

        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isBadRequest());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    @DisplayName("Test group creation with internal server error")
    @Tag("create-group")
    public void createGroup_InternalServerError() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new RuntimeException()).when(groupService).createGroup(username, groupName);

        mockMvc.perform(post("/api/v1/groups/{username}/{groupName}", username, groupName))
            .andExpect(status().isInternalServerError());

        verify(groupService).createGroup(username, groupName);
    }

    @Test
    @DisplayName("Test successful user assignment to group")
    @Tag("assign-user")
    public void assignUserToGroup_Success() throws Exception {
        String groupName = "testGroup";
        String username = "testUser";
        doNothing().when(groupService).assignUserToGroup(anyString(), anyString());

        mockMvc.perform(post("/api/v1/groups/{groupName}/assignUser", groupName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
            .andExpect(status().isOk());

        verify(groupService).assignUserToGroup(username, groupName);
    }

    @Test
    @DisplayName("Test user assignment to group with bad request")
    @Tag("assign-user")
    public void assignUserToGroup_BadRequest() throws Exception {
        String groupName = "testGroup";
        String username = "testUser";
        doThrow(new IllegalArgumentException()).when(groupService).assignUserToGroup(anyString(), anyString());

        mockMvc.perform(post("/api/v1/groups/{groupName}/assignUser", groupName)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username))
            .andExpect(status().isBadRequest());

        verify(groupService).assignUserToGroup(username, groupName);
    }

    @Test
    @DisplayName("Test if group exists and returns true")
    @Tag("check-group")
    public void groupExists_ReturnsTrue() throws Exception {
        String groupName = "testGroup";
        when(groupService.groupExists(groupName)).thenReturn(true);

        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        verify(groupService).groupExists(groupName);
    }

    @Test
    @DisplayName("Test if group exists and returns false")
    @Tag("check-group")
    public void groupExists_ReturnsFalse() throws Exception {
        String groupName = "nonExistentGroup";
        when(groupService.groupExists(groupName)).thenReturn(false);

        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));

        verify(groupService).groupExists(groupName);
    }

    @Test
    @DisplayName("Test if group existence check throws an exception")
    @Tag("check-group")
    public void groupExists_ThrowsException() throws Exception {
        String groupName = "testGroup";
        when(groupService.groupExists(groupName)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/groups/exists/{groupName}", groupName))
            .andExpect(status().isInternalServerError());

        verify(groupService).groupExists(groupName);
    }
}