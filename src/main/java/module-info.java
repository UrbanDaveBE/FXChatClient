module local.dev.fxchatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.net.http;
    requires org.json;
    requires javafx.base;

    opens local.dev.fxchatclient to javafx.fxml;
    exports local.dev.fxchatclient;
    exports local.dev.fxchatclient.controller;
    opens local.dev.fxchatclient.controller to javafx.fxml;
}