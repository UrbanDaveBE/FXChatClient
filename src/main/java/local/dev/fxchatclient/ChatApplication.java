package local.dev.fxchatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        stage.setMinWidth(400);
        stage.setMinHeight(400);

        String cssPath = Objects.requireNonNull(getClass().getResource("chat-styles.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("FXChatClient - Login");
        stage.setScene(scene);
        stage.show();
    }
}
