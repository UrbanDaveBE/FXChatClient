package local.dev.fxchatclient.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import local.dev.fxchatclient.service.LoginService;

public class ChatController {


    @FXML private AnchorPane rootPane;

    private String token;
    private String hostAddress;
    private String port;

    @FXML
    public void initialize() {

        // Setup logout beim schliessen sobald die Stage verfügbar ist
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Fenster wird geschlossen, logout...");
                LoginService loginService = new LoginService();
                loginService.executeLogout(hostAddress, port, token);
            });
        });
    }

    public void setSessionData(String token, String hostAddress, String port) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
    }
}
