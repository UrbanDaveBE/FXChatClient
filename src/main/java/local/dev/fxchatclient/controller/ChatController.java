package local.dev.fxchatclient.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import local.dev.fxchatclient.model.ChatMessage;
import local.dev.fxchatclient.service.LoginService;
import local.dev.fxchatclient.service.ChatService;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.task.MessagePollingTask;
import local.dev.fxchatclient.task.UserListTask;
import local.dev.fxchatclient.util.DialogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ChatController {


    @FXML private VBox rootPane;
    @FXML private ListView<User> userListView;
    @FXML private ListView<ChatMessage> chatListView; // Switch von TextArea
    @FXML private TextField messageInput;
    @FXML private Button sendButton;


    private String token;
    private String hostAddress;
    private String port;
    private String username;

    private ChatService chatService;
    private ObservableList<User> userListObservable;

    private final LoginService loginService = new LoginService();

    private volatile boolean isPolling = true;
    private Thread userListThread;
    private ScheduledExecutorService executorService;
    private Future<?> userListPollingFuture;
    private Future<?> messagePollingFuture;

    private User selectedTargetUser; // Aktuell ausgewählter Chatpartner
    private final Map<User, ObservableList<ChatMessage>> chatHistory = new HashMap<>();


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
                    setStyle(null);
                } else {
                    setText(user.getUsername() + " (" + user.isOnline() + ")");
                    if(user.getHasUnreadMessages()){
                        setStyle("-fx-font-weight: bold");
                    } else{
                        setStyle(null);
                    }
                }
            }
        });

        chatListView.setCellFactory(lv->new ListCell<ChatMessage>(){
            @Override
            protected void updateItem(ChatMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-padding: 0;");
                    return;
                }

                Label messageLabel = new  Label(message.getMessage());
                messageLabel.setWrapText(true);
                VBox bubble = new VBox(messageLabel);
                bubble.getStyleClass().add("chat-bubble");

                HBox messageContainer = new HBox(bubble);
                messageContainer.setMaxWidth(chatListView.getWidth()-20);

                if(message.isOwnMessage(username)){
                    messageContainer.setAlignment(Pos.CENTER_RIGHT);
                    bubble.getStyleClass().add("self");
                }else{
                    messageContainer.setAlignment(Pos.CENTER_LEFT);
                    bubble.getStyleClass().add("partner");
                }
                setGraphic(messageContainer);
                setStyle("-fx-padding: 5px 10px;");
            }
        });

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleUserSelection(newSelection);
            }
        });

        // Setup logout beim schliessen
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                stopPolling();
                loginService.executeLogout(hostAddress, port, token);
            });
        });
    }

    private void stopPolling() {
        if (executorService != null) {
            System.out.println("[executorService]: Scheduled Executor wird heruntergefahren.");

            if (userListPollingFuture != null) {
                userListPollingFuture.cancel(true);
            }

            if (messagePollingFuture != null) {
                messagePollingFuture.cancel(true);
            }
            executorService.shutdownNow();
            System.out.println("[executorService]: Scheduled Executor gestoppt.");
        }
    }

    public void setSessionData(String token, String hostAddress, String port, String username) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;

        this.chatService = new ChatService(token, hostAddress, port);
        startPolling();
    }

    private void startPolling() {
        executorService = Executors.newScheduledThreadPool(2);
        final int POLLING_RATE_SECONDS_MSG = 3;
        final int POLLING_RATE_SECONDS_UL = 60;
        updateUserListExecutor(POLLING_RATE_SECONDS_UL);
        updateChatMessages(POLLING_RATE_SECONDS_MSG);

    }

    private void updateChatMessages(int rate) {
        Runnable messageTask = new MessagePollingTask(this.chatService,this);
        // https://www.geeksforgeeks.org/java/scheduledexecutorservice-interface-in-java/
        messagePollingFuture = executorService.scheduleAtFixedRate(
                messageTask,
                0,
                rate,
                TimeUnit.SECONDS
        );
        System.out.println("[executorService]: Scheduled Polling für Messages gestartet.");

    }

    private void updateUserListExecutor(int rate) {
        Runnable userListTask = new UserListTask(this.chatService, this.userListObservable);
        // https://www.geeksforgeeks.org/java/scheduledexecutorservice-interface-in-java/
        userListPollingFuture = executorService.scheduleAtFixedRate(
                userListTask,
                0,
                rate,
                TimeUnit.SECONDS
        );
        System.out.println("[executorService]: Scheduled Polling für UserList gestartet.");

    }
    // für dokumentationszwecke da, BLOCKIERENDE Methode -> zu Thread bzw. Executor
    private void updateUserListGUI() {
        List<User> users = chatService.getUserStatusList();
        if (users.isEmpty()) {
            System.out.println("Keine Benutzer gefunden oder Fehler bei der Anfrage.");
        } else {
            userListObservable.clear();
            userListObservable.addAll(users);
            users.forEach(user ->
                    System.out.println(user.getUsername() + " ; Status: " + user.isOnline())
            );
        }
    }
    // für dokumentationszwecke da, wie man mittels while-Schlaufe
    private void updateUserListGUIAsync(){
        Runnable userListTask = new Runnable() {
            @Override
            public void run() {
                while (isPolling) {

                    List<User> users = chatService.getUserStatusList();

                    Platform.runLater(() -> {
                        if (users.isEmpty()) {
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

                    try{
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        isPolling = false;
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        };

        userListThread = new Thread(userListTask);
        userListThread.start();
    }

    @FXML
    private void handleSendMessage() {
        final String message = messageInput.getText().trim();
        User targetUser = this.selectedTargetUser;
        if(message.isEmpty()){return;}
        if (targetUser == null) {return;}
        executorService.execute(() -> {
            boolean success = chatService.sendMessage(targetUser, message);

            Platform.runLater(() -> {
                if(success){
                    ChatMessage sentMsg = new ChatMessage(message,username);
                    processSentMessage(sentMsg, targetUser);
                    System.out.println("[handleSendMessage]: Nachricht an ["+targetUser.getUsername()+"] gesendet: " + message);
                    messageInput.clear();
                } else{
                    DialogUtil.showAlert(Alert.AlertType.ERROR, "Fehler", "Nachricht konnte nicht gesendet werden.");
                }
            });
        });
    }

    private void processSentMessage(ChatMessage sentMsg, User targetUser) {
        chatHistory.computeIfAbsent(targetUser, k -> FXCollections.observableArrayList()).add(sentMsg);
    }


    @FXML
    private void handleUserSelection(User selectedUser) {
        // TODO
        this.selectedTargetUser = selectedUser;
        ObservableList<ChatMessage> history = chatHistory.getOrDefault(selectedTargetUser, FXCollections.observableArrayList());
        chatListView.setItems(history);

        selectedUser.setHasUnreadMessages((false));

        Platform.runLater(() -> {
            chatListView.scrollTo(history.size() - 1);
        });

        messageInput.setDisable(false);
        sendButton.setDisable(false);

    }

    public void processNewMessages(List<ChatMessage> newMessages) {
        if (newMessages == null || newMessages.isEmpty()) {
            return;
        }

        for (ChatMessage msg : newMessages) {

            User senderUser = userListObservable.stream()
                    .filter(u-> u.getUsername().equals(msg.getSenderUsername()))
                            .findFirst()
                                    .orElseGet(() ->{
                                        User newUser = new User(msg.getSenderUsername(),true,false);
                                        userListObservable.add(newUser);
                                        return newUser;
                                    });

            ObservableList<ChatMessage> userHistory = chatHistory.computeIfAbsent(senderUser, k -> FXCollections.observableArrayList());
            userHistory.add(msg);

            if (selectedTargetUser != null && selectedTargetUser.equals(senderUser)){
                chatListView.scrollTo(userHistory.size() - 1);
            } else{
                senderUser.setHasUnreadMessages((true));
            }
        }
    }


}