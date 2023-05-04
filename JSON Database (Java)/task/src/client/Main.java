package client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34721;
    public static void main(String[] args) {

        Args requestArgs = new Args();
        JCommander.newBuilder().addObject(requestArgs).build().parse(args);

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        ) {

            System.out.println("Client started!");

            // Send request to server
            Request request = new Request(requestArgs.type, requestArgs.index);

            if (requestArgs.type.equals("set")) {
                request.setValue(requestArgs.text);
            }

            output.writeUTF(request.toString());
            System.out.printf("Sent: %s%n", request);

            // Read response from server
            String response = input.readUTF();
            System.out.printf("Received: %s%n", response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

