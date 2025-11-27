package local.dev.fxchatclient.task;

import javafx.application.Platform;
import local.dev.fxchatclient.controller.ChatController;
import local.dev.fxchatclient.model.ChatMessage;
import local.dev.fxchatclient.service.ChatService;

import java.util.List;

public class MessagePollingTask implements Runnable {

    private final ChatService chatService;
    private final ChatController chatController;

    public MessagePollingTask(ChatService chatService, ChatController chatController) {
        this.chatService = chatService;
        this.chatController = chatController;
    }
    @Override
    public void run() {
        //TODO: Logik für pollMessages()
        List<ChatMessage> newMessages = chatService.readMessages();


        if (newMessages != null && !newMessages.isEmpty()) {
            Platform.runLater(() -> {
                chatController.processNewMessages(newMessages);
            });
        }
    }


}
