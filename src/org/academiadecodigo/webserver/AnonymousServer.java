package org.academiadecodigo.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Created by codecadet on 09/11/16.
 */
public class AnonymousServer {

    private final Socket clientSocket;

    AnonymousServer(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) {

        int count = 1;

        System.out.println("Opening server...");

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {

                final AnonymousServer server = new AnonymousServer(serverSocket.accept());
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        String root = "index.html";
                        String header;
                        String line = "";
                        String[] array;
                        File file = null;
                        byte[] fileBytes;

                        try {

                            DataOutputStream out = new DataOutputStream(server.clientSocket.getOutputStream());
                            BufferedReader in = new BufferedReader(new InputStreamReader(server.clientSocket.getInputStream()));

                            System.out.println(Thread.currentThread().getName());

                            line = in.readLine();
                            array = line.split("/");

                            System.out.println("Analysing request...");

                            if (!array[0].equals("GET ")) {
                                System.out.println("Unsuported verb: " + array[0]);
                                out.writeBytes("HTTP/1.1 405 Method Not Allowed\r\n");
                                out.writeBytes("Allow: GET\n\n");
                                System.out.println("Closing connection");
                                closeClient();
                            }

                            String request = getRequest(array[1]);

                            if (request.equals("")) {
                                request = root;
                            }

                            File resource = new File(request);
                            String extension = getExtension(request);

                            if (!resource.exists()) {
                                resource = new File("404.html");
                                fileBytes = getBytes(resource);
                                header = noutFoundHeader(fileBytes.length);
                            } else {
                                System.out.println(resource);
                                fileBytes = getBytes(resource);
                                header = headerBuilder(fileBytes.length, extension);
                            }

                            out.write(header.getBytes());
                            out.write(fileBytes);
                            out.flush();
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                    }

                    public void closeClient(){

                        if(!server.clientSocket.isClosed()){
                            try {
                                server.clientSocket.close();
                                System.out.println("Client closed");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    public String getExtension(String request){

                        return request.substring(request.lastIndexOf(".") + 1);
                    }

                    public String getRequest(String fullRequest){

                        String[] splitRequest = fullRequest.split(" ");

                        return splitRequest[0];
                    }

                    public String noutFoundHeader(int fileSize){

                        return "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Type: text/html\r\n" +
                                "Content-Length: " + fileSize + "\r\n\r\n";
                    }

                    public String headerBuilder(String code, int fileSize, String extension){

                        String statusCode = "";
                        String fileType = "";

                        if(code.equals("page")){
                            statusCode = "200 Document Follows";
                            fileType = "text/html";
                        } else if(code.equals(("image"))){
                            statusCode = "200 Image Follows";
                            fileType = "image/" + extension;
                        }
                        else{
                            statusCode = "404 Not Found";
                            fileType = "text/html";
                        }
                        return "HTTP/1.1" + statusCode + "\r\n" +
                                "Content-Type: " + fileType + "\r\n" +
                                "Content-Length: " + fileSize + "\r\n\r\n";

                    }

                    public String headerBuilder(int fileSize, String extension){

                        String statusCode = "";
                        String fileType = "";

                        if(extension.equals("html")){
                            statusCode = "200 Document Follows";
                            fileType = "text/" +  extension;
                        } else if(extension.equals("png")){
                            statusCode = "200 Image Follows";
                            fileType = "image/" + extension;
                        }
                        return "HTTP/1.1" + statusCode + "\r\n" +
                                "Content-Type: " + fileType + "\r\n" +
                                "Content-Length: " + fileSize + "\r\n\r\n";

                    }

                    public byte[] getBytes(File file){

                        try{
                            if(file.exists())
                                return Files.readAllBytes(file.toPath());
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        return new byte[1];

                    }

                });
                thread.setName("Client Thread #" + count);
                thread.start();

                count++;
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
