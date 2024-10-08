package persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import core.User;

public class UserHandlerTest {

    private UserHandler userHandler;
    private User user;
    private Path testFolderPath;

    @BeforeEach
    public void setUp() throws IOException {
        // Use a test-specific directory to avoid interfering with real data
        this.testFolderPath = Paths.get("src", "main", "java", "persistence", "users", "tests");

        // Initialize ToDoListHandler with the test path
        toDoListHandler = new ToDoListHandler(testFolderPath.toString() + File.separator);

        user = new User("testUser", "password123");

        // Create the test directory if it doesn't exist
        if (!Files.exists(testFolderPath)) {
            Files.createDirectories(testFolderPath);
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Delete the content in the test directory
        if (Files.exists(testFolderPath)) {
            Files.walk(testFolderPath)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    @Test
    void testConfirmNewValidUser() {

    }

    @Test
    void testGetUser() {

    }

    @Test
    void testGetUserValidationErrorMessage() {

    }

    @Test
    void testLoadUser() {

    }

    @Test
    void testSaveUser() {

    }

    @Test
    void testUpdateUser() {

    }

    @Test
    void testUserExists() {

    }
}
