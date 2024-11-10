package api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import core.GroupChat;
import core.Message;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class GroupChatServiceTest {

    @TempDir
    Path tempDir;

    private Path groupChatTestFolderPath;
    private GroupChatService groupChatService;

    @BeforeEach
    public void setUp() throws IOException {
        this.groupChatTestFolderPath = tempDir.resolve("groupchat");
        Files.createDirectories(groupChatTestFolderPath);

        groupChatService = new GroupChatService(groupChatTestFolderPath);
    }

    @AfterEach
    public void tearDown() throws IOException {
        deleteDirectory(tempDir);
    }

    private void deleteDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder())
                      .map(Path::toFile)
                      .forEach(file -> {
                          if (!file.delete()) {
                              System.out.println("Failed to delete: " + file.getAbsolutePath());
                          }
                      });
            }
        }
    }

    @Test
    @DisplayName("Create group chat successfully")
    @Tag("groupchat")
    public void testCreateGroupChat_Success() {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        Path groupChatFilePath = groupChatTestFolderPath.resolve(groupName + ".json");
        assertTrue(Files.exists(groupChatFilePath));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            GroupChat groupChat = mapper.readValue(groupChatFilePath.toFile(), GroupChat.class);
            assertNotNull(groupChat);
            assertTrue(groupChat.getMessages().isEmpty());
        } catch (IOException e) {
            fail("Failed to read the group chat file.");
        }
    }

    @Test
    @DisplayName("Create group chat that already exists")
    @Tag("groupchat")
    public void testCreateGroupChat_AlreadyExists() {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            groupChatService.createGroupChat(groupName);
        });

        String expectedMessage = "Group chat with name '" + groupName + "' already exists.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get group chat successfully")
    @Tag("groupchat")
    public void testGetGroupChat_Success() {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        GroupChat groupChat = groupChatService.getGroupChat(groupName);
        assertNotNull(groupChat);
        assertTrue(groupChat.getMessages().isEmpty());
    }

    @Test
    @DisplayName("Get group chat that does not exist")
    @Tag("groupchat")
    public void testGetGroupChat_DoesNotExist() {
        String groupName = "nonExistentGroup";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            groupChatService.getGroupChat(groupName);
        });

        String expectedMessage = "Group chat with name '" + groupName + "' does not exist.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Send message to group chat successfully")
    @Tag("groupchat")
    public void testSendMessage_Success() {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        Message message = new Message("testUser", "Hello, World!");
        groupChatService.sendMessage(groupName, message);

        GroupChat groupChat = groupChatService.getGroupChat(groupName);
        assertNotNull(groupChat);
        assertEquals(1, groupChat.getMessages().size());
        assertEquals(message, groupChat.getMessages().get(0));
    }

    @Test
    @DisplayName("Send message to non-existent group chat")
    @Tag("groupchat")
    public void testSendMessage_GroupChatDoesNotExist() {
        String groupName = "nonExistentGroup";
        Message message = new Message("testUser", "Hello, World!");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            groupChatService.sendMessage(groupName, message);
        });

        String expectedMessage = "Group chat with name '" + groupName + "' does not exist.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get messages from group chat")
    @Tag("groupchat")
    public void testGetMessages_Success() {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        Message message1 = new Message("user1", "First message");
        Message message2 = new Message("user2", "Second message");
        groupChatService.sendMessage(groupName, message1);
        groupChatService.sendMessage(groupName, message2);

        List<Message> messages = groupChatService.getMessages(groupName);
        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals(message1, messages.get(0));
        assertEquals(message2, messages.get(1));
    }

    @Test
    @DisplayName("Get messages from non-existent group chat")
    @Tag("groupchat")
    public void testGetMessages_GroupChatDoesNotExist() {
        String groupName = "nonExistentGroup";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            groupChatService.getMessages(groupName);
        });

        String expectedMessage = "Group chat with name '" + groupName + "' does not exist.";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Get group chat when file is corrupted")
    @Tag("groupchat")
    public void testGetGroupChat_FileIsCorrupted() throws IOException {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);

        // Corrupt the group chat file
        Path groupChatFilePath = groupChatTestFolderPath.resolve(groupName + ".json");
        Files.writeString(groupChatFilePath, "This is not valid JSON");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            groupChatService.getGroupChat(groupName);
        });

        String expectedMessage = "Failed to load group chat";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
@   DisplayName("Test constructor with default path")
    @Tag("constructor")
    void testDefaultConstructor() throws IOException {
        // Create required default directory
        Path defaultPath = Paths.get("..", "persistence", "src", "main", "java", 
                "persistence", "groupchat").toAbsolutePath().normalize();
        Files.createDirectories(defaultPath);

        // Define test user file paths
        String testUsername = "defaultTestUser";
        Path userFile = defaultPath.resolve(testUsername + ".json");
        
        try {
            // Create service with default constructor
            GroupChatService defaultService = new GroupChatService();
            
            // Test functionality
            String groupName = "defaultTestGroup";
            defaultService.createGroupChat(groupName);
            
            // Verify file was created in default location
            assertTrue(Files.exists(defaultPath.resolve(groupName + ".json")));
            
        } finally {
            // Cleanup
            if (Files.exists(userFile)) {
                deleteDirectory(userFile);
            }
        }
    }

    @Test
    @DisplayName("Test sending message persists correctly")
    @Tag("groupchat")
    void testSendMessagePersistence() throws IOException {
        String groupName = "testGroup";
        groupChatService.createGroupChat(groupName);
        
        Message message = new Message("testUser", "Test message");
        groupChatService.sendMessage(groupName, message);
        
        // Read the file directly to verify persistence
        Path groupChatFilePath = groupChatTestFolderPath.resolve(groupName + ".json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        GroupChat savedChat = mapper.readValue(groupChatFilePath.toFile(), GroupChat.class);
        assertEquals(1, savedChat.getMessages().size());
        assertEquals(message.getAuthor(), savedChat.getMessages().get(0).getAuthor());
        assertEquals(message.getText(), savedChat.getMessages().get(0).getText());
    }
}