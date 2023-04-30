package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private String[] dataBase = new String[1000];
    private static final int PORT = 34721;
    volatile static boolean stopFlag = false;

    public static void previousTask(String[] args) {
        Main base = new Main();
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }

            String option = "";
            int index = 0;
            StringBuilder text = new StringBuilder();

            try {
                String[] inputArray = input.split(" ");
                option = inputArray[0];
                index = Integer.parseInt(inputArray[1]) - 1;
                for (int i = 2; i < inputArray.length; i++) {
                    text.append(inputArray[i]).append(" ");
                }
            } catch (IndexOutOfBoundsException e) {
            }


            switch (option) {
                case "set" -> base.set(index, text.toString());
                case "get" -> base.get(index);
                case "delete" -> base.delete(index);
                default -> System.out.println("ERROR");
            }
        }
    }


    public boolean indexValidation(int index) {
        if (index < 0 || index > 999) {
            return false;
        }
        return true;
    }

    public String set(int index, String text) {
        String response = "";
        if (!indexValidation(index)) {
            response = "ERROR";
        } else {
            dataBase[index] = text;
            response = "OK";
        }
        return response;
    }

    public String get(int index) {
        String response = "";
        if (!indexValidation(index)) {
            response = "ERROR";
        } else if (dataBase[index] == null) {
            response = "ERROR";
        } else {
            response = dataBase[index];
        }
        return response;
    }

    public String delete(int index) {
        String response = "";
        if (!indexValidation(index)) {
            response = "ERROR";
        } else {
            dataBase[index] = null;
            response = "OK";
        }
        return response;
    }

    public static void main(String[] args) throws IOException {


        Main dataBaseController = new Main();

//        try (ServerSocket server = new ServerSocket(PORT)) {
//            System.out.println("Server started!");
//            try (
//                    Socket socket = server.accept();
//                    DataInputStream input = new DataInputStream(socket.getInputStream());
//                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//                ) {
//                String receivedMsg = input.readUTF();
//                System.out.printf("Received: %s%n", receivedMsg);
//                String msg = "A record # 12 was sent!";
//                output.writeUTF(msg);
//                System.out.printf("Sent: %s%n", msg);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started!");
        while (!stopFlag) { // Loop until stopFlag is set to true
            Socket clientSocket = serverSocket.accept(); // Wait for incoming connection
            new Thread(() -> {
                try {
                    DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    // Read request from client
                    String request = input.readUTF();
                    // Handle request
                    // -t get -i 1
                    String response = "";

                    if(request.equals("exit")) {
                        stopFlag = true;
                        response = "OK";
                        output.writeUTF(response);
                        System.out.printf("Sent: %s%n", response);
                        System.out.printf("Received: %s%n", request);
                        serverSocket.close();
                        exit(0);
                    }

                    String[] requestParts = request.split(" ");

                    int index = Integer.parseInt(requestParts[1]);
                    if (requestParts[0].equals("get")) {
                        // Handle get request
                        response = dataBaseController.get(index);
                    } else if (requestParts[0].equals("set")) {
                        // Handle set request
                        String text = "";
                        for (int j = 2; j < requestParts.length; j++) {
                            text += requestParts[j] + " ";
                        }
                        response = dataBaseController.set(index, text);
                    } else if (requestParts[0].equals("delete")) {
                        // Handle delete request
                        response = dataBaseController.delete(index);
                    } else {
                        response = "Invalid request";
                    }

                    // Send response to client
                    output.writeUTF(response);
                    System.out.printf("Sent: %s%n", response);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }
}