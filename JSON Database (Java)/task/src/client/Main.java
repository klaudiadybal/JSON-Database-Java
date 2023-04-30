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
//            String msg = "Give me a record # 12";
//            output.writeUTF(msg);
//            String recivedMsg = input.readUTF();
//            System.out.printf("Sent: %s%n", msg);
//            System.out.printf("Received: %s%n", recivedMsg);

            if (requestArgs.type.equals("exit")) {
                String exitRequest = "exit";
                output.writeUTF(exitRequest);
                System.out.printf("Sent: %s%n", exitRequest);
                String response = input.readUTF();
                System.out.printf("Received: %s%n", response);
                return;
            }

            // Send request to server
            String request = requestArgs.type + " " + requestArgs.index;
            if (requestArgs.type.equals("set")) {
                request += " " + requestArgs.text;
            }
            output.writeUTF(request);
            System.out.printf("Sent: %s%n", request);

            // Read response from server
            String response = input.readUTF();
            System.out.printf("Received: %s%n", response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
