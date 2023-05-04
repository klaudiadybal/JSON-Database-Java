package client;

public class Request {
    private String type;
    private int key;
    private String value;

    public Request (String type, int key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Request (String type, int key) {
        this.type = type;
        this.key = key;
        this.value = null;
    }

    public Request(String type) {
        this.type = type;
        this.key = 0;
        this.value = null;
    }

    public Request() {
        this.type = null;
        this.key = 0;
        this.value = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
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
        String output = String.format("{\"type\":\"%s\"", this.type);
        if (this.key != 0) {
            output += String.format(",\"key\":\"%d\"", this.key);
        }

        if (this.value!=null) {
            output += String.format(",\"value\":\"%s\"", this.value);
        }

        output += "}";

        return output;
    }
}
