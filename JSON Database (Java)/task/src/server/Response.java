package server;

public class Response {
    private String response;
    private String reason;
    private String value;

    public Response() {}

//    public Response(String response, String value, String reason) {
//        this.response = response;
//        this.value = value;
//        this.reason = reason;
//    }

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
        String output = String.format("{\"response\":\"%s\"", this.response);
        if (this.value != null) {
            output += String.format(",\"value\":\"%s\"", this.value);
        }

        if (this.reason != null) {
            output += String.format(",\"reason\":\"%s\"", this.reason);
        }

        output += "}";

        return output;
    }
}
