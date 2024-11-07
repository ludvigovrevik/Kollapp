module api {
    requires transitive core;
    requires persistence;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;
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
    requires java.base;

    exports api.controller;
    exports api.service;
    exports api;

    opens api.controller to spring.core, spring.beans, spring.context, spring.web, com.fasterxml.jackson.databind;
    opens api.service to spring.core, spring.beans, spring.context, com.fasterxml.jackson.databind;
    opens api to spring.core, spring.beans, spring.context, spring.web, com.fasterxml.jackson.databind;

}
