module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    opens app to javafx.graphics, javafx.fxml;
}
