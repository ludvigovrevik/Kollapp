package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class for the KollApp application.
 */
public class KollApp extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage.
     *
     * @param stage the primary stage for this application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("LoginScreen.fxml"));
        stage.setTitle("KollApp");
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}