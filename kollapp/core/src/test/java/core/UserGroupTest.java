package core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link UserGroup} class.
 */
@Tag("usergroup")
public class UserGroupTest {

    private UserGroup userGroup;
    private static final String GROUP_NAME = "Study Group";

    @BeforeEach
    @DisplayName("Initialize UserGroup before each test")
    void setUp() {
        // Initialize a UserGroup object before each test
        userGroup = new UserGroup(GROUP_NAME);
    }

    @Test
    @DisplayName("Test adding valid users to the group")
    @Tag("add")
    void testAddUser() {
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
    @DisplayName("Test adding duplicate users to the group")
    @Tag("add")
    void testAddDuplicateUser() {
        String user1 = "john_doe";
        userGroup.addUser(user1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(user1));
        assertEquals("User already exists in the group.", exception.getMessage());
    }

    @Test
    @DisplayName("Test adding invalid users (null or empty)")
    @Tag("add")
    void testAddInvalidUser() {
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(null));
        assertEquals("Username cannot be null or empty.", nullException.getMessage());

        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> userGroup.addUser(""));
        assertEquals("Username cannot be null or empty.", emptyException.getMessage());
    }

    @Test
    @DisplayName("Test removing valid users from the group")
    @Tag("remove")
    void testRemoveUser() {
        String user1 = "john_doe";
        userGroup.addUser(user1);
        assertTrue(userGroup.containsUser(user1), "User should be present in the group");

        userGroup.removeUser(user1);
        assertFalse(userGroup.containsUser(user1), "User should no longer be present in the group");
    }

    @Test
    @DisplayName("Test removing non-existent users from the group")
    @Tag("remove")
    void testRemoveNonExistentUser() {
        String user1 = "john_doe";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(user1));
        assertEquals("User does not exist in the group.", exception.getMessage());
    }

    @Test
    @DisplayName("Test removing invalid users (null or empty)")
    @Tag("remove")
    void testRemoveInvalidUser() {
        Exception nullException = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(null));
        assertEquals("Username cannot be null or empty.", nullException.getMessage());

        Exception emptyException = assertThrows(IllegalArgumentException.class, () -> userGroup.removeUser(""));
        assertEquals("Username cannot be null or empty.", emptyException.getMessage());
    }

    @Test
    @DisplayName("Test getting the number of users in the group")
    @Tag("getter")
    void testGetNumberOfUsers() {
        userGroup.addUser("john_doe");
        userGroup.addUser("jane_doe");

        assertEquals(2, userGroup.getNumberOfUsers(), "The group should contain 2 users");
    }

    @Test
    @DisplayName("Test checking if the group contains a specific user")
    @Tag("getter")
    void testContainsUser() {
        String user1 = "john_doe";
        userGroup.addUser(user1);

        assertTrue(userGroup.containsUser(user1), "The group should contain john_doe");
        assertFalse(userGroup.containsUser("unknown_user"), "The group should not contain unknown_user");
        assertThrows(IllegalArgumentException.class, () -> userGroup.containsUser(null));
        assertThrows(IllegalArgumentException.class, () -> userGroup.containsUser(""));
    }

    @Test
    @DisplayName("Test setting a new group name")
    @Tag("setter")
    void testSetGroupName() {
        String newGroupName = "New Study Group";
        userGroup.setGroupName(newGroupName);

        assertEquals(newGroupName, userGroup.getGroupName(), "The group name should be updated");
    }

    @Test
    @DisplayName("Test UserGroup default constructor initializes an empty list")
    @Tag("constructor")
    void testDefaultConstructor() {
        UserGroup defaultGroup = new UserGroup();

        assertNotNull(defaultGroup.getUsers(), "The users list should not be null");
        assertEquals(0, defaultGroup.getUsers().size(), "The users list should be empty");
    }
}
