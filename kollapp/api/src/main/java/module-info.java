module api {    
    requires java.base;
    requires core;
    requires persistence;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    // Spring dependencies
    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.security.crypto;

    requires java.sql;
    requires java.naming;
    requires java.instrument;

    // Open packages to Spring for dependency injection and component scanning
    opens api.controller to spring.core, spring.beans, spring.context, spring.web;
    opens api.service to spring.core, spring.beans, spring.context;
    opens api to spring.core, spring.beans, spring.context, spring.web;
    
    exports api.controller;
    exports api.service;
    exports api;
}