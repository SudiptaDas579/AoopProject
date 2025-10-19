module org.example.aoopproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.graphics;
    requires org.json;
    requires jdk.jsobject;
    requires com.google.gson;
    requires javafx.base;
    requires jakarta.mail;

    requires com.gluonhq.maps;
    requires java.net.http;


    opens org.example.aoopproject to javafx.fxml;
    exports org.example.aoopproject;
}