package local.dev.fxchatclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXML;

public class LoginController {

    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private Button pingButton;
    @FXML private Button connectButton;

    @FXML
    private void handlePingAction() {
        // TODO
        System.out.println("Ping-Button geklickt.");
    }

    @FXML
    private void handleConnectAction() {
        // TODO
        System.out.println("Connect-Button geklickt.");
    }
}
