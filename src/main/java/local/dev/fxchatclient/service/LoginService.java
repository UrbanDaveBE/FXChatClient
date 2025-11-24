package local.dev.fxchatclient.service;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;


public class LoginService extends HttpService{

    //singleton
    //private static final HttpClient client = HttpClient.newBuilder().build();
    //private static final String BASE_URL_PATTERN = "http://%s:%s";

    public void executePing(String hostAddress, String port) {
        try {
            // Read command-line parameters, if they exist

            //URI uri = new URI("http://" + hostAddress + ":" + port + "/ping"); // If needed, change http to https
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/ping");
            sendGetRequest(uri);

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void executeRegister(String hostAddress, String port, String username, String password) {
        try {
            // Read command-line parameters, if they exist

            //URI uri = new URI("http://" + hostAddress + ":" + port + "/user/register"); // If needed, change http to https
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/user/register");

            JSONObject jsonBody = new JSONObject()
                    .put("username", username)
                    .put("password", password);
            sendPostRequest(uri, jsonBody);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public JSONObject executeLogin(String hostAddress, String port, String username, String password) {
        try {
            // Read command-line parameters, if they exist

            //URI uri = new URI("http://" + hostAddress + ":" + port + "/user/login"); // If needed, change http to https
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/user/login");

            JSONObject jsonBody = new JSONObject()
                    .put("username", username)
                    .put("password", password);
            return sendPostRequest(uri, jsonBody);

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void executeLogout(String hostAddress, String port, String token) {
        try {
            // Read command-line parameters, if they exist

            //URI uri = new URI("http://" + hostAddress + ":" + port + "/user/logout"); // If needed, change http to https
            URI uri = new URI(String.format(BASE_URL_PATTERN, hostAddress, port) + "/user/logout");

            JSONObject jsonBody = new JSONObject()
                    .put("token", token);
            sendPostRequest(uri, jsonBody);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
