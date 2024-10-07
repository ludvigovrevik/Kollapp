module core {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    
    opens core to com.fasterxml.jackson.databind;
    exports core;
}