package api.controller;

import api.service.UserService;
import core.User;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Tag("controller")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    @DisplayName("Initialize MockMvc and ObjectMapper before each test")
    private void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test successful user save")
    @Tag("save-user")
    public void saveUser_Success() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        doNothing().when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk());

        verify(userService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Test user save with bad request")
    @Tag("save-user")
    public void saveUser_BadRequest() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        doThrow(new IllegalArgumentException()).when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isBadRequest());

        verify(userService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Test user save with internal server error")
    @Tag("save-user")
    public void saveUser_InternalServerError() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        doThrow(new RuntimeException()).when(userService).saveUser(any(User.class));

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isInternalServerError());

        verify(userService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Test successful user removal")
    @Tag("remove-user")
    public void removeUser_Success() throws Exception {
        String username = "testUser";
        doNothing().when(userService).removeUser(anyString());

        mockMvc.perform(delete("/api/v1/users/{username}", username))
            .andExpect(status().isOk());

        verify(userService).removeUser(username);
    }

    @Test
    @DisplayName("Test user removal with bad request")
    @Tag("remove-user")
    public void removeUser_BadRequest() throws Exception {
        String username = "testUser";
        doThrow(new IllegalArgumentException()).when(userService).removeUser(anyString());

        mockMvc.perform(delete("/api/v1/users/{username}", username))
            .andExpect(status().isBadRequest());

        verify(userService).removeUser(username);
    }

    @Test
    @DisplayName("Test if user exists and returns true")
    @Tag("user-exists")
    public void userExists_ReturnsTrue() throws Exception {
        String username = "testUser";
        when(userService.userExists(username)).thenReturn(true);

        mockMvc.perform(get("/api/v1/users/exists/{username}", username))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        verify(userService).userExists(username);
    }

    @Test
    @DisplayName("Test if user exists and returns false")
    @Tag("user-exists")
    public void userExists_ReturnsFalse() throws Exception {
        String username = "testUser";
        when(userService.userExists(username)).thenReturn(false);

        mockMvc.perform(get("/api/v1/users/exists/{username}", username))
            .andExpect(status().isOk())
            .andExpect(content().string("false"));

        verify(userService).userExists(username);
    }

    @Test
    @DisplayName("Test user exists check with bad request")
    @Tag("user-exists")
    public void userExists_BadRequest() throws Exception {
        String username = "testUser";
        when(userService.userExists(username)).thenThrow(new IllegalArgumentException("Invalid username"));

        mockMvc.perform(get("/api/v1/users/exists/{username}", username))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test user exists check with internal server error")
    @Tag("user-exists")
    public void userExists_InternalServerError() throws Exception {
        String username = "testUser";
        when(userService.userExists(username)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/users/exists/{username}", username))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Test successful user load for login")
    @Tag("load-user")
    public void loadUser_Success() throws Exception {
        String username = "testUser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        when(userService.loadUser(anyString(), anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/v1/users/login")
                .param("username", username)
                .param("password", password))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userService).loadUser(username, password);
    }

    @Test
    @DisplayName("Test unauthorized user load for login")
    @Tag("load-user")
    public void loadUser_Unauthorized() throws Exception {
        String username = "testUser";
        String password = "password";
        when(userService.loadUser(anyString(), anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/users/login")
                .param("username", username)
                .param("password", password))
            .andExpect(status().isUnauthorized());

        verify(userService).loadUser(username, password);
    }

    @Test
    @DisplayName("Test successful new user validation confirmation")
    @Tag("validate-user")
    public void confirmNewValidUser_Success() throws Exception {
        String username = "testUser";
        String password = "password";
        String confirmPassword = "password";
        when(userService.confirmNewValidUser(anyString(), anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/v1/users/validate")
                .param("username", username)
                .param("password", password)
                .param("confirmPassword", confirmPassword))
            .andExpect(status().isOk())
            .andExpect(content().string("true"));

        verify(userService).confirmNewValidUser(username, password, confirmPassword);
    }

    @Test
    @DisplayName("Test user validation error message retrieval")
    @Tag("validate-user")
    public void getUserValidationErrorMessage() throws Exception {
        String username = "testUser";
        String password = "password";
        String confirmPassword = "different";
        String errorMessage = "Passwords do not match";
        when(userService.getUserValidationErrorMessage(anyString(), anyString(), anyString()))
            .thenReturn(errorMessage);

        mockMvc.perform(post("/api/v1/users/validate/message")
                .param("username", username)
                .param("password", password)
                .param("confirmPassword", confirmPassword))
            .andExpect(status().isOk())
            .andExpect(content().string(errorMessage));

        verify(userService).getUserValidationErrorMessage(username, password, confirmPassword);
    }

    @Test
    @DisplayName("Test successful group assignment to user")
    @Tag("assign-group")
    public void assignGroupToUser_Success() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doNothing().when(userService).assignGroupToUser(anyString(), anyString());

        mockMvc.perform(post("/api/v1/users/{username}/assignGroup", username)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("groupName", groupName))
            .andExpect(status().isOk());

        verify(userService).assignGroupToUser(username, groupName);
    }

    @Test
    @DisplayName("Test group assignment to user with bad request")
    @Tag("assign-group")
    public void assignGroupToUser_BadRequest() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new IllegalArgumentException()).when(userService).assignGroupToUser(anyString(), anyString());

        mockMvc.perform(post("/api/v1/users/{username}/assignGroup", username)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("groupName", groupName))
            .andExpect(status().isBadRequest());

        verify(userService).assignGroupToUser(username, groupName);
    }

    @Test
    @DisplayName("Test group assignment to user with internal server error")
    @Tag("assign-group")
    public void assignGroupToUser_InternalServerError() throws Exception {
        String username = "testUser";
        String groupName = "testGroup";
        doThrow(new RuntimeException()).when(userService).assignGroupToUser(anyString(), anyString());

        mockMvc.perform(post("/api/v1/users/{username}/assignGroup", username)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("groupName", groupName))
            .andExpect(status().isInternalServerError());

        verify(userService).assignGroupToUser(username, groupName);
    }
}