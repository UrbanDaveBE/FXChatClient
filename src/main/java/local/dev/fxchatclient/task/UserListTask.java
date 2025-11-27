package local.dev.fxchatclient.task;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.service.ChatService;

import java.util.List;

public class UserListTask implements Runnable {

    private ChatService chatService;
    @FXML private TextArea chatArea;
    private ObservableList<User> userListObservable;


    public UserListTask(ChatService chatService, ObservableList<User> userListObservable, TextArea chatArea) {
        this.chatService = chatService;
        this.userListObservable = userListObservable;
        this.chatArea = chatArea;
    }

    @Override
    public void run() {
        List<User> users = chatService.getUserStatusList();

        Platform.runLater(() -> {
            if (users.isEmpty()) {
                chatArea.setText("FEHLER: Konnte keine Benutzerliste vom Server laden.");
                System.out.println("Keine Benutzer gefunden oder Fehler bei der Anfrage.");
            } else {
                userListObservable.clear();
                userListObservable.addAll(users);

                System.out.println("Benutzerliste erfolgreich in GUI geladen. Gefundene User: " + users.size());
                users.forEach(user ->
                        System.out.println(user.getUsername() + " ; Status: " + user.isOnline())
                );
            }
        });
    }

}
