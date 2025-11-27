package local.dev.fxchatclient.model;

import org.json.JSONObject;

public class ChatMessage {
    private final String message;
    private final String senderUsername;

    public ChatMessage(String message, String senderUsername){
        this.message = message;
        this.senderUsername = senderUsername;
    }
    public ChatMessage(JSONObject json){
        this.message = json.getString("message");
        this.senderUsername = json.getString("username");
    }

    public String getMessage() {
        return this.message;
    }
    public String getSenderUsername() {
        return this.senderUsername;
    }

    public String toString(){
        return "["+this.senderUsername+"]: "+this.message;
    }

    public boolean isOwnMessage(String currentClientUsername) {
        return this.senderUsername.equals(currentClientUsername);
    }
}
