package local.dev.fxchatclient.task;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.service.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListTask implements Runnable {

    private ChatService chatService;
    private ObservableList<User> userListObservable;


    public UserListTask(ChatService chatService, ObservableList<User> userListObservable) {
        this.chatService = chatService;
        this.userListObservable = userListObservable;
    }

    @Override
    public void run() {
        List<User> users = chatService.getUserStatusList();

        Platform.runLater(() -> {
            if (users.isEmpty()) {
                System.out.println("Keine Benutzer gefunden oder Fehler bei der Anfrage.");
            } else {
                Map<String, User> existingUserMap = new HashMap<>();
                userListObservable.forEach(user -> existingUserMap.put(user.getUsername(), user));
                userListObservable.clear();
                //userListObservable.addAll(users);

                System.out.println("Benutzerliste erfolgreich in GUI geladen. Gefundene User: " + users.size());
                users.forEach(user -> {
                    System.out.println(user.getUsername() + " ; Status: " + user.isOnline());
                    User existing = existingUserMap.get(user.getUsername());
                    if(existing != null) {
                        existing.setOnline(user.isOnline());
                        userListObservable.add(existing);
                    } else{
                        userListObservable.add(user);
                    }
                });
            }
        });
    }

}
