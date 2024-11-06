package ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Unit tests for the KollAppController class.
 */
@ExtendWith(ApplicationExtension.class)
@Tag("ui")
public class KollAppControllerTest {

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

    /**
     * Initializes the test environment by loading the Kollektiv.fxml and setting up the controller.
     *
     * @param stage the primary stage for JavaFX tests
     * @throws Exception if FXML loading fails
     */
    @Start
    private void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Kollektiv.fxml"));
        Parent root = loader.load();

        KollAppController controller = loader.getController();
        User user = new User("KollAppControllerUserTest", "passwordd");
        controller.initializeToDoList(user);
        controller.populateGroupView(user.getUserGroups());

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Verifies that the "+" button is present and has the correct text.
     *
     * @param robot the FxRobot instance for simulating user interactions
     */
    @Test
    @DisplayName("Test presence of '+' button in KollAppController UI")
    @Tag("ui")
    public void shouldContainAddButton(FxRobot robot) {
        // Verify that the "+" button is present
        Button addButton = robot.lookup("#addButton").queryAs(Button.class);
        Assertions.assertThat(addButton).isNotNull();
        Assertions.assertThat(addButton).hasText("+ Add task");
    }
}
