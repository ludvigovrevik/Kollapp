package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserGroupTest {

    private UserGroup userGroup;
    private static final String GROUP_NAME = "Study Group";

    @BeforeEach
    void setUp() {
        // Initialize a UserGroup object before each test
        userGroup = new UserGroup(GROUP_NAME);
    }

    @Test
    void testAddUser() {
        // Test adding a valid user
        String user1 = "john_doe";
        String user2 = "jane_doe";

        userGroup.addUser(user1);
        userGroup.addUser(user2);

        List<String> users = userGroup.getUsers();
        assertEquals(2, users.size(), "There should be 2 users in the group");
        assertTrue(users.contains(user1), "User list should contain john_doe");
        assertTrue(users.contains(user2), "User list should contain jane_doe");
    }

    @Test
    void testAddDuplicateUser() {
        // Test adding the same user twice
        String user1 = "john_doe";
        userGroup.addUser(user1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(user1));
        assertEquals("User already exists in the group.", exception.getMessage());
    }

    @Test
    void testAddInvalidUser() {
        // Test adding null or empty users
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(null));
        assertEquals("Username cannot be null or empty.", nullException.getMessage());

        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(""));
        assertEquals("Username cannot be null or empty.", emptyException.getMessage());
    }

    @Test
    void testRemoveUser() {
        // Test removing an existing user
        String user1 = "john_doe";
        userGroup.addUser(user1);
        assertTrue(userGroup.containsUser(user1), "User should be present in the group");

        userGroup.removeUser(user1);
        assertFalse(userGroup.containsUser(user1), "User should no longer be present in the group");
    }

    @Test
    void testRemoveNonExistentUser() {
        // Test removing a user that doesn't exist
        String user1 = "john_doe";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(user1));
        assertEquals("User does not exist in the group.", exception.getMessage());
    }

    @Test
    void testRemoveInvalidUser() {
        // Test removing null or empty users
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(null));
        assertEquals("Username cannot be null or empty.", nullException.getMessage());

        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(""));
        assertEquals("Username cannot be null or empty.", emptyException.getMessage());
    }

    @Test
    void testGetNumberOfUsers() {
        // Test number of users in the group
        userGroup.addUser("john_doe");
        userGroup.addUser("jane_doe");

        assertEquals(2, userGroup.getNumberOfUsers(), "The group should contain 2 users");
    }

    @Test
    void testContainsUser() {
        // Test whether the group contains a user
        String user1 = "john_doe";
        userGroup.addUser(user1);

        assertTrue(userGroup.containsUser(user1), "The group should contain john_doe");
        assertFalse(userGroup.containsUser("unknown_user"), "The group should not contain unknown_user");
        assertThrows(IllegalArgumentException.class, () -> userGroup.containsUser(null));
        assertThrows(IllegalArgumentException.class, () -> userGroup.containsUser(""));
    }

    @Test
    void testSetGroupName() {
        // Test setting a group name
        String newGroupName = "New Study Group";
        userGroup.setGroupName(newGroupName);

        assertEquals(newGroupName, userGroup.getGroupName(), "The group name should be updated");
    }

    @Test
    void testDefaultConstructor() {
        // Test the default constructor
        UserGroup defaultGroup = new UserGroup();

        assertNotNull(defaultGroup.getUsers(), "The users list should not be null");
        assertEquals(0, defaultGroup.getUsers().size(), "The users list should be empty");
    }
}
