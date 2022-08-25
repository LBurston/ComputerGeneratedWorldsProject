module ComputerGeneratedWorldsProject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.web;
    requires javafx.media;
    requires org.apache.commons.lang3;
    requires org.jetbrains.annotations;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
//    requires api;
//    requires client;

    opens com.cgw to javafx.fxml;
    opens com.cgw.controllers to javafx.fxml;

    exports com.cgw;
    exports com.cgw.controllers;
    exports com.cgw.features;
    exports com.cgw.generators;
    exports com.cgw.generators.feature;
    exports com.cgw.relationships;
    exports com.cgw.exceptions;
}

