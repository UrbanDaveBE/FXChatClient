package local.dev.fxchatclient.task;

import javafx.application.Platform;
import javafx.collections.ObservableList;

import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.service.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserListTask implements Runnable {


    private final ChatService chatService;
    private final ObservableList<User> userListObservable;


    public UserListTask(ChatService chatService, ObservableList<User> userListObservable) {
        this.chatService = chatService;
        this.userListObservable = userListObservable;
    }

    @Override
    public void run() {
        System.out.println("[UserListTask]: run gestartet.");

        List<User> users = chatService.getUserStatusList();

        Platform.runLater(() -> {
            if (users.isEmpty()) {
                System.out.println("[WARNING]-[UserListTask]: No users found");
            } else {

                User selectedUserBeforeUpdate = userListObservable.stream()
                        .filter(u -> !u.getHasUnreadMessages())
                        .findFirst()
                        .orElse(null);

                String selectedUsername = null;
                User currentSelection = userListObservable.stream()
                        .filter(u -> !u.getHasUnreadMessages())
                        .findFirst()
                        .orElse(null);
                if (currentSelection != null) {
                    selectedUsername = currentSelection.getUsername();
                }

                Map<String, User> existingUserMap = new HashMap<>();
                userListObservable.forEach(user -> existingUserMap.put(user.getUsername(), user));

                userListObservable.clear();

                //TODO: DEBUG ONLY
                //System.out.println("Verarbeite " + users.size() + " User vom Server.");

                users.forEach(user -> {

                    //TODO: DEBUG ONLY
                    //System.out.println("User: " + user.getUsername() + " ; Status: " + user.isOnline());

                    User existing = existingUserMap.get(user.getUsername());
                    if(existing != null) {
                        existing.setOnline(user.isOnline());
                        userListObservable.add(existing);
                    } else{
                        userListObservable.add(user);
                    }
                });
                System.out.println("[UserListTask]: run beendet. User-Liste aktualisiert. Anzahl: " + users.size());
            }
        });
    }

}
