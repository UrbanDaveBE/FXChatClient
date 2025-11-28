package local.dev.fxchatclient.service;


import local.dev.fxchatclient.model.ChatMessage;
import local.dev.fxchatclient.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatService extends HttpService {

    private final String token;
    private final String hostAddress;
    private final String port;

    public ChatService(String token, String hostAddress, String port) {
        this.token = token;
        this.hostAddress = hostAddress;
        this.port = port;
    }

    public List<ChatMessage> readMessages(){
        List<ChatMessage> messages = new ArrayList<>();
        try {
            // Read command-line parameters, if they exist

            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/chat/poll");
            JSONObject jsonBody = new JSONObject()
                    .put("token", token);

            // TODO: prüfen ob noch online!
            JSONObject response = sendPostRequest(uri, jsonBody);
            if (response != null && response.has("messages")) {
                JSONArray messageArray = response.getJSONArray("messages");
                for (int i = 0; i < messageArray.length(); i++) {
                    JSONObject messageJson = messageArray.getJSONObject(i);
                    messages.add(new ChatMessage(messageJson));
                }
            } else  {
                System.err.println("[Error]-[readMessages]: beim Polling der Nachrichten");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return messages;

    }
    public boolean sendMessage(User targetUser, String message) {
        List<User> users = new ArrayList<>();
        boolean responsev = true;

        try {
            // Read command-line parameters, if they exist

            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/chat/send");
            JSONObject jsonBody = new JSONObject()
                    .put("token", token)
                    .put("username", targetUser.getUsername())
                    .put("message", message);

            // TODO: prüfen ob noch online!
            JSONObject response = sendPostRequest(uri, jsonBody);

            if (response != null && response.has("send")) { // {"send":true}
                responsev = response.getBoolean("send");
            } else { // {"send":false}
                responsev = response.getBoolean("send");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return responsev;
    }

    private List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        try {
            // Read command-line parameters, if they exist
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/users");
            JSONObject response = sendGetRequest(uri);

            if (response != null && response.has("users")) {
                JSONArray userArray = response.getJSONArray("users");
                for (int i = 0; i < userArray.length(); i++) {
                    users.add(new User(userArray.getString(i)));
                }
            } else if (response != null && response.has("Error")) {
                System.out.println(response.getString("Error"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return users;
    }

    private List<User> getAllOnlineUsers(){
        List<User> users = new ArrayList<>();
        try{
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/users/online");
            JSONObject response = sendGetRequest(uri);
            if (response != null && response.has("online")) {
                JSONArray userArray = response.getJSONArray("online");
                for (int i = 0; i < userArray.length(); i++) {
                    users.add(new User(userArray.getString(i)));
                }
            } else if (response != null && response.has("Error")) {
                System.out.println(response.getString("Error"));
            }
        } catch (Exception e){
            System.out.println(e);
        }
        return users;
    }

    public List<User> getUserStatusList(){
        List<User> allUsers = getAllUsers();
        List<User> allOnlineUsers = getAllOnlineUsers();

        Map<String, User> onlineMap = new HashMap<>();
        for (User user : allOnlineUsers) {
            onlineMap.put(user.getUsername(), user);
        }

        for (User user : allUsers) {
            if (onlineMap.containsKey(user.getUsername())) {
                user.setOnline(true);
            }
        }

        return allUsers;
    }
}
