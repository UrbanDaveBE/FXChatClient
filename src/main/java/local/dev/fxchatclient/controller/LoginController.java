package local.dev.fxchatclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import local.dev.fxchatclient.ChatApplication;
import local.dev.fxchatclient.service.LoginService;
import local.dev.fxchatclient.util.DialogUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML private TextField addressField;
    @FXML private TextField portField;
    @FXML private Button loginButton;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label pingStatusLabel;


    @FXML
    private void handlePingAction() {
        System.out.println("[handlePingAction]: Ping-Button geklickt.");
        LoginService loginService = new LoginService();
        JSONObject response = loginService.executePing(addressField.getText(),portField.getText());
        if (response != null && response.has("ping")){
            boolean pingSuccess = response.getBoolean("ping");

            if (pingSuccess) {
                pingStatusLabel.setText("Ping: OK");
            } else {
                pingStatusLabel.setText("Ping: Error (Ungültige Antwort)");
            }
        } else{
            pingStatusLabel.setText("Ping: Error (Verbindungsfehler)");
        }
    }

    @FXML
    private void handleRegisterAction() {
        System.out.println("[handleRegisterAction]: Register-Button geklickt.");
        LoginService loginService = new LoginService();
        JSONObject response = loginService.executeRegister(addressField.getText(),portField.getText(),usernameField.getText(),passwordField.getText());

        if (response != null && response.has("username")){
            DialogUtil.showAlert(Alert.AlertType.INFORMATION, "Registrierung erfolgreich", "Der Benutzer '" + response.getString("username") + "' wurde erfolgreich registriert!");
        } else if (response != null && response.has("Error")){
            DialogUtil.showAlert(Alert.AlertType.ERROR, "Registrierung fehlgeschlagen", response.getString("Error"));
        } else{
            DialogUtil.showAlert(Alert.AlertType.ERROR, "Fehler", "Registrierung fehlgeschlagen: Unerwartete Serverantwort oder Verbindungsfehler.");
        }

    }

    public void handleLoginAction(ActionEvent actionEvent) {
        System.out.println("[handleLoginAction]: Login-Button geklickt.");
        LoginService loginService = new LoginService();
        JSONObject response = loginService.executeLogin(addressField.getText(), portField.getText(), usernameField.getText(), passwordField.getText());

        if (response != null && response.has("token")) {
            String token = response.getString("token");
            System.out.println("[handleLoginAction]: TOKEN: " + token);


            FXMLLoader loader = new FXMLLoader(ChatApplication.class.getResource("chat-view.fxml"));
            Scene scene;
            try {
                scene = new Scene(loader.load(), 800, 600);
                String cssPath = Objects.requireNonNull(ChatApplication.class.getResource("chat-styles.css")).toExternalForm();
                scene.getStylesheets().add(cssPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ChatController chatController = loader.getController();
            chatController.setSessionData(token, addressField.getText(), portField.getText(), usernameField.getText());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FXChatClient - " + usernameField.getText());

        } else if (response != null && response.has("Error")) {
            DialogUtil.showAlert(Alert.AlertType.ERROR, "Login fehlgeschlagen", response.getString("Error"));

        }
        else {
            DialogUtil.showAlert(Alert.AlertType.ERROR, "Fehler", "Login fehlgeschlagen: Unerwartete Serverantwort oder Verbindungsfehler.");
        }
    }
}
