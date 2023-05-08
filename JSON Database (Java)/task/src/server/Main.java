package server;

import client.Request;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private final String[] dataBase = new String[1000];
    private static final int PORT = 34721;
    private volatile static boolean stopFlag = false;
    private Response response = new Response();

    public static void main(String[] args) {
        Main dataBaseController = new Main();
        dataBaseController.serverStart();
    }


    public void serverStart() {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (!this.stopFlag) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                        DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                        String readUTF = "";
                        try {
                            readUTF = input.readUTF();
                        } catch (EOFException e) {
                            shutdown(serverSocket,clientSocket);
                        }
                            Gson gson = new Gson();
                            Request request = gson.fromJson(readUTF, Request.class);

                            if(request.getType().equals("exit")) {
                                shutdown(serverSocket, clientSocket);
                                return;
                            }

                            int index = Integer.parseInt(request.getKey());;

                            if (request.getType().equals("get")) {
                                this.response.setValue(this.get(index, this.response));

                            } else if (request.getType().equals("set")) {
                                String text = request.getValue();
                                this.response.setResponse(this.set(index, text));

                            } else if (request.getType().equals("delete")) {
                                this.response.setResponse(this.delete(index, this.response));
                            } else {
                                this.response.setResponse("Invalid request");
                            }

                            output.writeUTF(this.response.toString());
                            System.out.printf("Received: %s%n", request);
                            System.out.printf("Sent: %s%n", this.response);

                        } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        response.setResponse(null);
                        response.setReason(null);
                        response.setValue(null);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean indexValidation(int index) {
        if (index < 1 || index > 1000) {
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
            dataBase[index - 1] = text;
            response = "OK";
        }
        return response;
    }

    public String get(int index, Response response) {
        String value = null;
        if (!indexValidation(index)) {
            response.setReason("No such key");
            response.setResponse("ERROR");

        } else if (dataBase[index - 1] == null) {
            response.setResponse("ERROR");
            response.setReason("No such key");

        } else {
            value = dataBase[index - 1];
            response.setResponse("OK");
        }
        return value;
    }

    public String delete(int index, Response response) {
        String output = "";
        if (!indexValidation(index)) {
            output = "ERROR";
            response.setReason("No such key");
        } else if (dataBase[index - 1] == null){
            output = "ERROR";
            response.setReason("No such key");
        } else {
            dataBase[index - 1] = null;
            output = "OK";
        }
        return output;
    }


    public void shutdown(ServerSocket serverSocket, Socket clientSocket) {
        stopFlag = true;
        try {
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}