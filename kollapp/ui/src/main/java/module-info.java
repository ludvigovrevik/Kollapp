module ui {
    requires core;
    requires javafx.controls;
    requires javafx.fxml;

    opens KollApp to javafx.graphics, javafx.fxml;
}
