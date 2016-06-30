package com.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpService {
    public static void main(String args[]) throws IOException {
        ServerSocket server = new ServerSocket(9000, 50000);
        System.out.println("Server is up and running...");

        while (true) {
            Socket clientSocket = server.accept();
            new Thread(new Routes(clientSocket)).start();
        }
    }
}

