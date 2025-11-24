package local.dev.fxchatclient.service;

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



    public LoginService() {

    }

    public void executePing(String hostAddress, String port) {
        try {
            // Read command-line parameters, if they exist

            URI uri = new URI("http://" + hostAddress + ":" + port + "/ping"); // If needed, change http to https
            HttpClient client = HttpClient.newBuilder()
                    // .sslContext(SSLContext.getDefault()) // Uncomment if you want to use https
                    .build();

            sendGetRequest(uri, client);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Send a GET request to /ping
    private static void sendGetRequest(URI uri, HttpClient client) {
        String result;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .GET().build();
            HttpResponse<String> response;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = "GET Request: Status code = " + response.statusCode();
            String body = response.body();
            JSONObject jsonIn = readJSON(body);
            if (jsonIn == null) result += " (body is invalid JSON)";
            result += ", body: " + body;
        } catch (IOException e) {
            result = "GET Request: IO Exception: " + e;
        } catch (InterruptedException e) {
            result = "GET Request: Timeout";
        } catch (Exception e) {
            result = "GET Request: Unexpected error!";
        }
        System.out.println(result);
    }

    private static JSONObject readJSON(String in) {
        JSONObject jsonIn = null;
        try {
            jsonIn = new JSONObject(in);
        } catch (Exception e) {
            // If anything goes wrong, return null
        }
        return jsonIn;
    }
}
