package com.service;

import com.db.Database;
import com.routeHelpers.Monitor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpService {
    public static void main(String args[]) throws IOException {
        ServerSocket server = new ServerSocket(9000);
        Monitor.getInstance();
        Database database = new Database();

        System.out.println("Server is up and running...");

        while (true) {
            Socket clientSocket = server.accept();
            new Thread(new Routes(clientSocket, database)).start();
        }
    }
}
