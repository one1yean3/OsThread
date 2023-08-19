import java.io.*;
import java.net.*;

public class FileServer {

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(4444);
            System.out.println("Server started.");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection : " + clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
    }
}