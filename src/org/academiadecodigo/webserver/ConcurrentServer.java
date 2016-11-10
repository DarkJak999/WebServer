package org.academiadecodigo.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by codecadet on 09/11/16.
 */
public class ConcurrentServer {
    private static Socket clientSocket;

    /*public static void main(String[] args) {

        int count = 1;

        System.out.println("Opening server...");

        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            while (true) {
                clientSocket = serverSocket.accept();

                Thread thread = new Thread(new Client(clientSocket));
                thread.setName("Client Thread #" + count);
                thread.start();

                count++;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) {

        int count = 1;

        System.out.println("Opening server...");

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            ExecutorService pool = Executors.newFixedThreadPool(50);

            while (true) {
                clientSocket = serverSocket.accept();

                pool.submit(new Client(clientSocket, count));

                count++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
