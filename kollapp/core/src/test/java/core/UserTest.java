package core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for the {@link User} class.
 */
@Tag("user")
public class UserTest {

    private User user;

    @BeforeEach
    @DisplayName("Initialize User object before each test")
    void setUp() {
        user = new User("testUser", "testPassword");
    }

    @Test
    @DisplayName("Test User default constructor")
    @Tag("constructor")
    void testUserConstructor() {
        User user = new User();
        assertNotNull(user, "User object should not be null");
    }

    @Test
    @DisplayName("Test adding user groups")
    @Tag("add")
    void testAddUserGroup() {
        String group1 = "Group A";
        String group2 = "Group B";

        user.addUserGroup(group1);
        user.addUserGroup(group2);

        List<String> userGroups = user.getUserGroups();

        assertEquals(2, userGroups.size(), "There should be 2 user groups");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
        assertTrue(userGroups.contains(group2), "User groups should contain Group B");
    }

    @Test
    @DisplayName("Test adding duplicate user group")
    @Tag("add")
    void testAddUserGroup_DuplicateGroup() {
        String group1 = "Group A";

        user.addUserGroup(group1);
        user.addUserGroup(group1); // Add the same group again

        List<String> userGroups = user.getUserGroups();

        assertEquals(1, userGroups.size(), "There should still be only 1 user group");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
    }

    @Test
    @DisplayName("Test password retrieval")
    @Tag("getter")
    void testGetPassword() {
        String password = user.getPassword();
        assertEquals("testPassword", password, "Password should match the initialized value");
    }

    @Test
    @DisplayName("Test username retrieval")
    @Tag("getter")
    void testGetUsername() {
        String username = user.getUsername();
        assertEquals("testUser", username, "Username should match the initialized value");
    }

    @Test
    @DisplayName("Test retrieving user groups")
    @Tag("getter")
    void testGetUserGroups() {
        String group1 = "Group A";
        String group2 = "Group B";

        user.addUserGroup(group1);
        user.addUserGroup(group2);

        List<String> userGroups = user.getUserGroups();

        assertNotNull(userGroups, "User groups should not be null");
        assertEquals(2, userGroups.size(), "There should be 2 user groups");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
        assertTrue(userGroups.contains(group2), "User groups should contain Group B");
    }
}
