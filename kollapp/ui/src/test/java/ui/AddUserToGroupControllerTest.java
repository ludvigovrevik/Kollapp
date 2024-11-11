package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ui.api.GroupApiHandler;
import ui.api.UserApiHandler;

/**
 * Unit tests for the AddUserToGroupController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class AddUserToGroupControllerTest {

    private AddUserToGroupController controller;
    private UserApiHandler mockUserApiHandler;
    private GroupApiHandler mockGroupHandler;
    private User mockUser;
    private String mockUsername;

     // Headless mode is enabled
    static private boolean headless = true;

    /**
     * Sets up the environment for headless mode if the 'headless' flag is true.
     * This method configures various system properties required for running
     * JavaFX tests in a headless environment.
     * 
     * Properties set:
     * - testfx.headless: Enables TestFX headless mode.
     */
    @BeforeAll
    static void setupHeadlessMode() {
        if(headless){
            System.setProperty("testfx.headless", "true");

            System.setProperty("java.awt.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("testfx.robot", "glass");
        }
    }

    @Start
    public void start(Stage stage) throws Exception {
        mockGroupHandler = mock(GroupApiHandler.class);
        mockUser = mock(User.class);
        mockUserApiHandler = mock(UserApiHandler.class);

        when(mockUser.getUserGroups()).thenReturn(Arrays.asList("Group1", "Group2"));

        controller = new AddUserToGroupController();

        // Inject mockUserApiHandler
        Field userApiHandlerField = AddUserToGroupController.class.getDeclaredField("userApiHandler");
        userApiHandlerField.setAccessible(true);
        userApiHandlerField.set(controller, mockUserApiHandler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddUserToGroup.fxml"));
        loader.setControllerFactory(param -> controller);
        Parent root = loader.load();

        controller.initializeAddToUserGroup(mockUser);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setUp(FxRobot robot) throws Exception {
        mockGroupHandler = mock(GroupApiHandler.class);
        mockUser = mock(User.class);
        mockUsername = mockUser.getUsername();

        when(mockUser.getUserGroups()).thenReturn(Arrays.asList("Group1", "Group2"));

        controller = spy(new AddUserToGroupController());

        // Inject mockUserApiHandler
        Field userApiHandlerField = AddUserToGroupController.class.getDeclaredField("userApiHandler");
        userApiHandlerField.setAccessible(true);
        userApiHandlerField.set(controller, mockUserApiHandler);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AddUserToGroup.fxml"));
        loader.setControllerFactory(param -> controller);
        Parent root = loader.load();

        controller.initializeAddToUserGroup(mockUser);

        Stage stage = (Stage) robot.window(0);
        robot.interact(() -> {
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    /**
     * Tests that adding a user without selecting a group displays an appropriate error message.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test adding user without selecting a group")
    void testAddUserToGroup_NoGroupSelected(FxRobot robot) {
        UserApiHandler mockUserApiHandler = mock(UserApiHandler.class);
        controller.setUserApiHandler(mockUserApiHandler);
        
        when(mockUserApiHandler.userExists("existingUser")).thenReturn(true);
        
        robot.clickOn("#usernameField").write("existingUser");
        robot.clickOn("Add user"); 
        
        Label feedbackLabel = robot.lookup("#feedbackLabel").queryAs(Label.class);
        assertNotNull(feedbackLabel);
        assertEquals("No group selected.", feedbackLabel.getText());
    }
}