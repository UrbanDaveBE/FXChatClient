package local.dev.fxchatclient.task;

import javafx.application.Platform;
import local.dev.fxchatclient.controller.ChatController;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.service.ChatService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListTask implements Runnable {

    private final ChatService chatService;
    private final ChatController chatController;

    public UserListTask(ChatService chatService, ChatController chatController) {
        this.chatService = chatService;
        this.chatController = chatController;
    }

    @Override
    public void run() {
        List<User> users = chatService.getUserStatusList();

        Platform.runLater(() -> {
            if (users.isEmpty()) {
                System.out.println("[WARNING]-[UserListTask]: No users found");
                return;
            }

            users.sort((u1, u2) -> {

                int onlineCompare = Boolean.compare(u2.isOnline(), u1.isOnline());
                if (onlineCompare != 0) {
                    return onlineCompare;
                }
                return u1.getUsername().compareToIgnoreCase(u2.getUsername());
            });

            User currentlySelected = chatController.getUserListView().getSelectionModel().getSelectedItem();
            String selectedUsername = (currentlySelected != null) ? currentlySelected.getUsername() : null;


            Map<String, User> existingMap = new HashMap<>();
            for (User u : chatController.getUserListObservable()) {
                existingMap.put(u.getUsername(), u);
            }

            String currentSelfUser = chatController.getUsername();

            chatController.getUserListObservable().clear();

            for (User newUser : users) {
                if (currentSelfUser != null && newUser.getUsername().equals(currentSelfUser)) {
                    continue;
                }

                User existing = existingMap.get(newUser.getUsername());
                if (existing != null) {
                    existing.setOnline(newUser.isOnline());

                    chatController.getUserListObservable().add(existing);
                } else {
                    chatController.getUserListObservable().add(newUser);
                }
            }

            if (selectedUsername != null) {
                for (User u : chatController.getUserListObservable()) {
                    if (u.getUsername().equals(selectedUsername)) {
                        chatController.getUserListView().getSelectionModel().select(u);
                        break;
                    }
                }
            }
        });
    }
}