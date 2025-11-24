package local.dev.fxchatclient.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.service.ChatService;
import local.dev.fxchatclient.service.LoginService;

import java.util.List;

public class ChatController {


    @FXML private AnchorPane rootPane;

    private String token;
    private String hostAddress;
    private String port;
    private String username;

    private ChatService chatService;


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

    public void setSessionData(String token, String hostAddress, String port, String username) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;
        chatService = new ChatService(token,hostAddress,port,username);
        chatService.getUserStatusList();
        List<User> users = chatService.getUserStatusList();

        if (users.isEmpty()) {
            System.out.println("Keine Benutzer gefunden");
        } else {
            System.out.println("Benutzerliste und Status:");
            users.forEach(user ->
                    System.out.println(user.getUsername() + " ; Status: " + user.isOnline())
            );
        }
    }
}
