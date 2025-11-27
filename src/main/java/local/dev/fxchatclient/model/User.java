package local.dev.fxchatclient.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;

public class User {
    private final String username;
    private BooleanProperty isOnline;
    private BooleanProperty hasUnreadMessages;


    public User(String username){
        this.username = username;
        this.isOnline = new SimpleBooleanProperty(false);
        this.hasUnreadMessages = new SimpleBooleanProperty(false);
    }

    public User(String username, boolean isOnline, boolean hasUnreadMessages){
        this.username = username;
        this.isOnline = new SimpleBooleanProperty(isOnline);
        this.hasUnreadMessages = new SimpleBooleanProperty(hasUnreadMessages);
    }
    public String getUsername() {
        return username;
    }
    public boolean isOnline() {
        return this.isOnline.get();
    }
    public void setOnline(boolean online){
        this.isOnline.set(online);
    }

    public boolean getHasUnreadMessages() {
        return hasUnreadMessages.get();
    }

    public BooleanProperty isOnlineProperty() {
        return isOnline;
    }

    public BooleanProperty hasUnreadMessagesProperty() {
        return hasUnreadMessages;
    }

    public void setHasUnreadMessages(boolean hasUnreadMessages) {
        this.hasUnreadMessages.set(hasUnreadMessages);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username + (isOnline() ? " (Online)" : " (Offline)");
    }
}
