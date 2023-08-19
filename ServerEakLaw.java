import java.net.*;
import java.io.*;
public class ServerEakLaw {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(25000);
        Socket clientSocket; 
        try {
            while(true){
                clientSocket = serverSocket.accept();
                new Thread(new TestClientHandler(clientSocket)).run();
            }
        }catch(Exception e){
            System.out.println("Error");
            e.printStackTrace();
        }
        serverSocket.close();
    }
}
