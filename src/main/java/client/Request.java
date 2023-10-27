package client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Request {

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private String type;
    private String key;
    private String value;

    public Request (String type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Request (String type, String key) {
        this.type = type;
        this.key = key;
        // this.value = null;
    }

    public Request(String type) {
        this.type = type;
        // this.key = null;
        // this.value = null;
    }

    public Request() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        Gson gson = gsonBuilder
//                .setPrettyPrinting()
                .create();

        return gson.toJson(this);
    }
}
