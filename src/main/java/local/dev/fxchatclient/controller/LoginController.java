package local.dev.fxchatclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import local.dev.fxchatclient.service.LoginService;

public class LoginController {

    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private Button pingButton;
    @FXML private Button connectButton;

    @FXML
    private void handlePingAction() {
        // TODO
        System.out.println("Ping-Button geklickt.");
        LoginService loginService = new LoginService();
        loginService.executePing(addressField.getText(),portField.getText());
    }

    @FXML
    private void handleConnectAction() {
        // TODO
        System.out.println("Connect-Button geklickt.");
    }
}
