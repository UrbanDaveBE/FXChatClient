package local.dev.fxchatclient.util;

import org.json.JSONObject;

public class JsonUtil {

    public static JSONObject readJSON(String raw){
        JSONObject jsonIn = null;
        try {
            jsonIn = new JSONObject(raw);
        } catch (Exception e) {
            // If anything goes wrong, return null
            System.out.println("JSON Parse Error");
        }
        return jsonIn;
    }
}
