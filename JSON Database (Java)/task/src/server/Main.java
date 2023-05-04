package server;

import client.Request;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private final String[] dataBase = new String[1000];
    private static final int PORT = 34721;
    volatile static boolean stopFlag = false;
    private Response response = new Response();

    public static void main(String[] args) {
        Main main = new Main();
        main.serverStart();
    }


    public void serverStart() {

        Main dataBaseController = new Main();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (!stopFlag) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                        DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                        String readUTF = input.readUTF();
                        Request request = new Request();

                        String [] requestParts = readUTF.split("\"");
                        try {
                            request.setType(requestParts[3]);
                            request.setKey(Integer.parseInt(requestParts[7]));
                            request.setValue(requestParts[11]);
                        } catch (IndexOutOfBoundsException e) {
                            request.setValue(null);
                        }


                        if(request.getType().equals("exit")) {
                            stopFlag = true;
                            this.response.setResponse("OK");
                            output.writeUTF(this.response.toString());
                            System.out.printf("Sent: %s%n", this.response);
                            System.out.printf("Received: %s%n", request);
                            // exit(0);
                            serverSocket.close();
                        }

                        int index = request.getKey();
                        if (request.getType().equals("get")) {
                            this.response.setValue(dataBaseController.get(index, this.response));

                        } else if (request.getType().equals("set")) {
                            String text = request.getValue();
                            this.response.setResponse(dataBaseController.set(index, text));

                        } else if (request.getType().equals("delete")) {
                            this.response.setResponse(dataBaseController.delete(index, this.response));
                        } else {
                            this.response.setResponse("Invalid request");
                        }

                        output.writeUTF(this.response.toString());
                        System.out.printf("Received: %s%n", request);
                        System.out.printf("Sent: %s%n", this.response);
                        clientSocket.close();

                        response.setResponse(null);
                        response.setReason(null);
                        response.setValue(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean indexValidation(int index) {
        if (index < 0 || index > 999) {
            this.response.setReason("No such key");
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

    public String get(int index, Response response) {
        String value = null;
        if (!indexValidation(index)) {
            response.setReason("No such key");
            response.setResponse("ERROR");

        } else if (dataBase[index] == null) {
            response.setResponse("ERROR");
            response.setReason("No such key");

        } else {
            value = dataBase[index];
            response.setResponse("OK");
        }
        return value;
    }

    public String delete(int index, Response response) {
        String output = "";
        if (!indexValidation(index)) {
            output = "ERROR";
            response.setReason("No such key");
        } else if (dataBase[index] == null){
            output = "ERROR";
            response.setReason("No such key");
        } else {
            dataBase[index] = null;
            output = "OK";
        }
        return output;
    }
}