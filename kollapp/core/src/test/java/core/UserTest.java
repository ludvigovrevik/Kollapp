package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Initialize a User object before each test
        user = new User("testUser", "testPassword");
    }

    @Test
    void testUserContructor() {
        User user = new User();
        assertNotNull(user, "User object should not be null");
    }

    @Test
    void testAddUserGroup() {
        // Test adding a user group
        String group1 = "Group A";
        String group2 = "Group B";
        
        user.addUserGroup(group1);
        user.addUserGroup(group2);

        List<String> userGroups = user.getUserGroups();
        
        // Assert that the user groups were added
        assertEquals(2, userGroups.size(), "There should be 2 user groups");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
        assertTrue(userGroups.contains(group2), "User groups should contain Group B");
    }

    @Test
    void testAddUserGroup_DuplicateGroup() {
        // Test adding a duplicate group
        String group1 = "Group A";
        
        user.addUserGroup(group1);
        user.addUserGroup(group1);  // Add the same group again

        List<String> userGroups = user.getUserGroups();
        
        // Assert that the duplicate group wasn't added
        assertEquals(1, userGroups.size(), "There should still be only 1 user group");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
    }

    @Test
    void testGetPassword() {
        // Test password retrieval
        String password = user.getPassword();
        assertEquals("testPassword", password, "Password should match the initialized value");
    }

    @Test
    void testGetUsername() {
        // Test username retrieval
        String username = user.getUsername();
        assertEquals("testUser", username, "Username should match the initialized value");
    }

    @Test
    void testGetUserGroups() {
        // Test retrieving user groups
        String group1 = "Group A";
        String group2 = "Group B";

        user.addUserGroup(group1);
        user.addUserGroup(group2);
        
        List<String> userGroups = user.getUserGroups();
        
        // Assert that the correct user groups are returned
        assertNotNull(userGroups, "User groups should not be null");
        assertEquals(2, userGroups.size(), "There should be 2 user groups");
        assertTrue(userGroups.contains(group1), "User groups should contain Group A");
        assertTrue(userGroups.contains(group2), "User groups should contain Group B");
    }
}