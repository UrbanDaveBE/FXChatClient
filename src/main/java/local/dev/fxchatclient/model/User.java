package local.dev.fxchatclient.model;

public class User {
    private final String username;
    private Boolean isOnline;

    public User(String username){
        this.username = username;
        this.isOnline = false;
    }

    User(String username, Boolean isOnline){
        this.username = username;
        this.isOnline = isOnline;
    }
    public String getUsername() {
        return username;
    }
    public Boolean isOnline() {
        return this.isOnline;
    }
    public void setOnline(Boolean online){
        this.isOnline = online;
    }
}
