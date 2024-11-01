module persistence {
    requires transitive core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.core;
    requires java.base;
    requires spring.context;
    requires spring.beans;

    exports persistence;
}
