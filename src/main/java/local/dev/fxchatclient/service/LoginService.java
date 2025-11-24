package local.dev.fxchatclient.service;

import local.dev.fxchatclient.util.JsonUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class LoginService {

    //singleton
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final String BASE_URL_PATTERN = "http://%s:%s";

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


    // Send a POST request to /ping
    private static JSONObject sendPostRequest(URI uri, JSONObject data) {
        String result;
        JSONObject jsonResponse = new JSONObject();
        try {

            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                    .build();
            HttpResponse<String> response;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = "POST Request: Status code = " + response.statusCode();
            String body = response.body();
            jsonResponse = JsonUtil.readJSON(body);
            if (jsonResponse == null) result += " (body is invalid JSON)";
            result += ", body: " + body;
        } catch (IOException e) {
            result = "POST Request: IO Exception" + e;
        } catch (InterruptedException e) {
            result = "POST Request: Timeout" + e;
        } catch (Exception e) {
            result = "POST Request: Unexpected error!" + e;
        }
        System.out.println(result);
        return jsonResponse;
    }
    // Send a GET request to /ping
    private static JSONObject sendGetRequest(URI uri) {
        String result;
        JSONObject jsonResponse = new JSONObject();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .GET().build();
            HttpResponse<String> response;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = "GET Request: Status code = " + response.statusCode();
            String body = response.body();
            jsonResponse = JsonUtil.readJSON(body);
            if (jsonResponse == null) result += " (body is invalid JSON)";
            result += ", body: " + body;
        } catch (IOException e) {
            result = "GET Request: IO Exception: " + e;
        } catch (InterruptedException e) {
            result = "GET Request: Timeout";
        } catch (Exception e) {
            result = "GET Request: Unexpected error!";
        }
        System.out.println(result);
        return jsonResponse;
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

            URI uri = new URI("http://" + hostAddress + ":" + port + "/user/login"); // If needed, change http to https


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
