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

public abstract class HttpService {
    //singleton
    protected static final HttpClient client = HttpClient.newBuilder().build();
    protected static final String BASE_URL_PATTERN = "http://%s:%s";

    protected static JSONObject sendPostRequest(URI uri, JSONObject payload){
        String result;
        JSONObject jsonResponse = new JSONObject();
        try {

            HttpRequest request = HttpRequest.newBuilder().uri(uri)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.of(3, ChronoUnit.SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
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

    protected static JSONObject sendGetRequest(URI uri) {
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
}
