module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    opens app to javafx.graphics, javafx.fxml;
}
