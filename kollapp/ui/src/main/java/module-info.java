module ui {
    requires transitive core;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires java.net.http;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.datatype.jsr310;

    opens api to com.fasterxml.jackson.databind;
    opens ui to javafx.graphics, javafx.fxml, api;

    exports ui;
    exports api;
}
