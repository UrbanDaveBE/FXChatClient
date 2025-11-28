package local.dev.fxchatclient.service;

import local.dev.fxchatclient.util.JsonUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class HttpService {
    //singleton
    protected static final HttpClient client = HttpClient.newBuilder().build();
    protected static final String BASE_URL_PATTERN = "http://%s:%s";

    protected static JSONObject sendPostRequest(URI uri, JSONObject payload){
        JSONObject jsonResponse = new JSONObject();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            System.out.println("HttpService: POST " + uri.getPath() + " -> Status: " + response.statusCode());
            //TODO DEBUG ONLY
            //System.out.println("HttpService: POST Response Body: " + body);
            jsonResponse = JsonUtil.readJSON(body);
            if (jsonResponse == null) {
                System.out.println("HttpService: POST Body is invalid JSON: " + body);
            }

        } catch (IOException e) {
            System.out.println("HttpService: POST Request IO Exception:" + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("HttpService: POST Request Timeout: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("HttpService: POST Request Unexpected error: " + e.getMessage());
        }
        return jsonResponse;
    }

    protected static JSONObject sendGetRequest(URI uri) {
        JSONObject jsonResponse = new JSONObject();
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            System.out.println("HttpService: GET " + uri.getPath() + " -> Status: " + response.statusCode());
            //TODO DEBUG ONLY
            //System.out.println("HttpService: GET Response Body: " + body);


            jsonResponse = JsonUtil.readJSON(body);
            if (jsonResponse == null) {
                System.out.println("HttpService: GET Body is invalid JSON: " + body);
            }

        } catch (IOException e) {
            System.out.println("HttpService: GET Request IO Exception: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("HttpService: GET Request Timeout: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("HttpService: GET Request Unexpected error: " + e.getMessage());
        }
        return jsonResponse;
    }
}