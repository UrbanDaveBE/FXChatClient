package local.dev.fxchatclient.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import local.dev.fxchatclient.service.LoginService;
import local.dev.fxchatclient.service.ChatService;
import local.dev.fxchatclient.model.User;

import java.util.List;

public class ChatController {


    @FXML private AnchorPane rootPane;
    @FXML private ListView<User> userListView;
    @FXML private TextArea chatArea;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;


    private String token;
    private String hostAddress;
    private String port;
    private String username;

    private ChatService chatService;
    private ObservableList<User> userListObservable;

    private final LoginService loginService = new LoginService();

    @FXML
    public void initialize() {
        userListObservable = FXCollections.observableArrayList();
        userListView.setItems(userListObservable);

        userListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUsername() + " (" + user.isOnline() + ")");
                }
            }
        });

        // Setup logout beim schliessen
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                System.out.println("Fenster wird geschlossen");
                loginService.executeLogout(hostAddress, port, token);
            });
        });
    }

    public void setSessionData(String token, String hostAddress, String port, String username) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;

        this.chatService = new ChatService(token, hostAddress, port, username);

        System.out.println("ChatController: Session gestartet.");
        updateUserListGUI();
    }

    private void updateUserListGUI() {
        List<User> users = chatService.getUserStatusList();
        if (users.isEmpty()) {
            chatArea.setText("FEHLER: Konnte keine Benutzerliste vom Server laden.");
            System.out.println("Keine Benutzer gefunden oder Fehler bei der Anfrage.");
        } else {
            userListObservable.clear();
            userListObservable.addAll(users);
            users.forEach(user ->
                    System.out.println(user.getUsername() + " ; Status: " + user.isOnline())
            );
        }
        System.out.println("----------------------------------------\n");
    }

    @FXML
    private void handleSendMessage() {
        // TODO
    }

    @FXML
    private void handleUserSelection() {
        // TODO
    }
}