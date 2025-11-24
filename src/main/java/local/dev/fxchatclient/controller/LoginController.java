package local.dev.fxchatclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import local.dev.fxchatclient.ChatApplication;
import local.dev.fxchatclient.service.LoginService;
import org.json.JSONObject;

import java.io.IOException;

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
        LoginService loginService = new LoginService();
        JSONObject response = loginService.executeLogin(addressField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());

        if (response != null && response.has("token")) {
            String token = response.getString("token");
            System.out.println("Login erfolgreich für, token erhalten: " + token);


            FXMLLoader loader = new FXMLLoader(ChatApplication.class.getResource("chat-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(loader.load(), 800, 600);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ChatController chatController = loader.getController();
            chatController.setSessionData(token, addressField.getText(), portField.getText(), usernameField.getText());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FXChatClient - " + usernameField.getText());

        } else {
            System.out.println("Login fehlgeschlagen!");
        }
    }
}
