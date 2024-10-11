package ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import core.ToDoList;
import core.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import persistence.GroupHandler;
import persistence.ToDoListHandler;


@ExtendWith(ApplicationExtension.class)
public class KollAppControllerTest {
    @Mock
    private ToDoList mockToDoList;

    @Mock
    private ToDoListHandler mockToDoListHandler;

    @Mock
    private GroupHandler mockGroupHandler;

    @Mock
    private User mockUser;

    private KollAppController controller;

    @Start
    private void start(Stage stage) throws Exception {
        // Load Kollektiv.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Kollektiv.fxml"));
        Parent root = loader.load();

        KollAppController controller = loader.getController();
        User user = new User("KollAppControllerUserTest", "passwordd");
        controller.innitializeToDoList(user); 
        controller.populateGroupView();

        // Set the scene and show the stage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void shouldContainAddButton(FxRobot robot) {
        // Verify that the "+" button is present
        robot.lookup("#addButton").queryAs(Button.class);
        Assertions.assertThat(robot.lookup("#addButton").queryAs(Button.class)).isNotNull();
        Assertions.assertThat(robot.lookup("#addButton").queryAs(Button.class)).hasText("+");
    }
}
