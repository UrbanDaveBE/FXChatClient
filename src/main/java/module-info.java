module local.dev.fxchatclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens local.dev.fxchatclient to javafx.fxml;
    exports local.dev.fxchatclient;
}