package local.dev.fxchatclient.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import local.dev.fxchatclient.service.LoginService;
import local.dev.fxchatclient.service.ChatService;
import local.dev.fxchatclient.model.User;
import local.dev.fxchatclient.task.UserListTask;

import java.util.List;
import java.util.concurrent.*;

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

    private volatile boolean isPolling = true;
    private Thread userListThread;
    private ScheduledExecutorService executorService;
    private Future<?> userListPollingFuture;

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
                //isPolling = false;
                //if(userListThread != null) {
                //   userListThread.interrupt();
                //}
                stopPolling();
                System.out.println("Fenster wird geschlossen");
                loginService.executeLogout(hostAddress, port, token);
            });
        });
    }

    private void stopPolling() {
        if (executorService != null) {
            System.out.println("Scheduled Executor wird heruntergefahren.");

            if (userListPollingFuture != null) {
                userListPollingFuture.cancel(true);
            }

            executorService.shutdownNow();
            System.out.println("Scheduled Polling gestoppt.");
        }
    }

    public void setSessionData(String token, String hostAddress, String port, String username) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
        this.username = username;

        this.chatService = new ChatService(token, hostAddress, port, username);

        System.out.println("ChatController: Session gestartet.");
        //updateUserListGUI();
        //updateUserListGUIAsync();
        updateUserListExecutor();
    }

    private void updateUserListExecutor() {
        executorService = Executors.newScheduledThreadPool(2);
        Runnable userListTask = new UserListTask(this.chatService, this.userListObservable, this.chatArea);
        // https://www.geeksforgeeks.org/java/scheduledexecutorservice-interface-in-java/
        userListPollingFuture = executorService.scheduleAtFixedRate(
                userListTask,
                0,
                120,
                TimeUnit.SECONDS
        );
        System.out.println("Scheduled Polling für UserList gestartet.");

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
    }

    private void updateUserListGUIAsync(){
        Runnable userListTask = new Runnable() {
            @Override
            public void run() {
                while (isPolling) {

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
        // TODO
        User targetUser = userListView.getSelectionModel().getSelectedItem();
        if (targetUser != null) {
            chatService.sendMessage(targetUser,messageInput.getText());
        }

    }

    @FXML
    private void handleUserSelection() {
        // TODO
    }
}