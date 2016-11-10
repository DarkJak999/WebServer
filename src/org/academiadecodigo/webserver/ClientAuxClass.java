package org.academiadecodigo.webserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Created by codecadet on 09/11/16.
 */
public class ClientAuxClass {

    public void closeClient(Socket clientSocket){

        if(!clientSocket.isClosed()){
            try {
                clientSocket.close();
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
}
