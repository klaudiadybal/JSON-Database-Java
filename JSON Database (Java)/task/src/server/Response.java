package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Response {
    private static GsonBuilder gsonBuilder = new GsonBuilder();

    private String response;
    private String value;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
