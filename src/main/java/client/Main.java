package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;



public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34721;
    public static void main(String[] args) {

        Args requestArgs = new Args();
        try {
            JCommander.newBuilder().addObject(requestArgs).build().parse(args);
        } catch (ParameterException e) {
            System.out.printf("Received:{\"type\":\"%s\",\"key\":\"%s\"}", requestArgs.type, requestArgs.index);
            return;
        }


        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

        ) {

            System.out.println("Client started!");
            // Send request to server
            try {
                Request request = new Request(requestArgs.type);

                if(requestArgs.index != null) {
                    request.setKey(requestArgs.index);
                }

                if (requestArgs.type.equals("set")) {
                    request.setValue(requestArgs.text);
                }

                output.writeUTF(request.toString());
                System.out.printf("Sent: %s%n", request);
                // Read response from server
                String response = "";
                try {
                    response = input.readUTF();
                } catch (EOFException e) {
                    return;
                }
                System.out.printf("Received: %s%n", response);

            } catch (NumberFormatException e) {}


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
