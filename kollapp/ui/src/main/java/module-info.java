module ui {
    requires transitive core;
    requires transitive persistence;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;

    opens ui to javafx.graphics, javafx.fxml;

    exports ui;
}
