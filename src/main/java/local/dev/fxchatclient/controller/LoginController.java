package local.dev.fxchatclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import local.dev.fxchatclient.service.LoginService;

public class LoginController {

    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private Button pingButton;
    @FXML private Button registerButton;
    @FXML private Button loginButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handlePingAction() {
        // TODO
        System.out.println("Ping-Button geklickt.");
        LoginService loginService = new LoginService();
        loginService.executePing(addressField.getText(),portField.getText());
    }

    @FXML
    private void handleRegisterAction() {
        // TODO
        System.out.println("Register-Button geklickt.");
        LoginService loginService = new LoginService();
        loginService.executeRegister(addressField.getText(),portField.getText(),usernameField.getText(),passwordField.getText());
    }

    public void handleLoginAction(ActionEvent actionEvent) {
        System.out.println("Login-Button geklickt.");
    }
}
