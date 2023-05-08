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

    public static void main(String[] args) {

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
                        Request request = new Gson().fromJson(readUTF, Request.class);

                        Response response = new Response();
                        if(request.getType().equals("exit")) {
                            stopFlag = true;
                            response.setResponse("OK");
                            output.writeUTF(response.toString());
                            System.out.printf("Sent: %s%n", response);
                            System.out.printf("Received: %s%n", request);
                            serverSocket.close();
                            exit(0);
                        }

                        int index = request.getKey();
                        if (request.getType().equals("get")) {
                            response.setValue(dataBaseController.get(index));

                        } else if (request.getType().equals("set")) {
                            String text = request.getValue();
                            response.setResponse(dataBaseController.set(index, text));

                        } else if (request.getType().equals("delete")) {
                            response.setResponse(dataBaseController.delete(index));
                        } else {
                            response.setResponse("Invalid request");
                        }

                        output.writeUTF(response.toString());
                        System.out.printf("Received: %s%n", request);
                        System.out.printf("Sent: %s%n", response);
                        clientSocket.close();

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
}