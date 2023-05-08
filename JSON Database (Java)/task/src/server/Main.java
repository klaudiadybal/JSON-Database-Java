package server;

import client.Request;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
//

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private final Map<String, String> dataBase = new HashMap<>(1000);
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

                            String key = request.getKey();

                            if (request.getType().equals("get")) {
                                this.response.setValue(this.get(key, this.response));

                            } else if (request.getType().equals("set")) {
                                String text = request.getValue();
                                this.response.setResponse(this.set(key, text));

                            } else if (request.getType().equals("delete")) {
                                this.response.setResponse(this.delete(key, this.response));
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

    public boolean keyValidation(String key) {
        Set<String> keySet = dataBase.keySet();
        if (keySet.contains(key)) {
            return true;
        }
        response.setResponse("No such key");
        return false;
    }

    public String set(String key, String text) {
        dataBase.put(key,text);
        String response = "OK";
        return response;
    }

    public String get(String key, Response response) {
        String value = null;
        if (!keyValidation(key)) {
            response.setReason("No such key");
            response.setResponse("ERROR");
        } else if (dataBase.get(key) == null) {
            response.setResponse("ERROR");
            response.setReason("No such key");
        } else {
            value = dataBase.get(key);
            response.setResponse("OK");
        }
        return value;
    }

    public String delete(String key, Response response) {
        String output = "";
        if (!keyValidation(key)) {
            output = "ERROR";
            response.setReason("No such key");
        } else if (dataBase.get(key) == null) {
            output = "ERROR";
            response.setReason("No such key");
        } else {
            dataBase.put(key, null);
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