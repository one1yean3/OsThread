import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.io.*;

public class Server {
    public static void main(String[] args) {
        final int PORT = 55555;
        System.out.println("Starting server..");
        try (ServerSocketChannel serverSC = ServerSocketChannel.open();) {
            ServerSocket serverS = serverSC.socket();
            serverS.bind(new InetSocketAddress(PORT));
            System.out.println("Listening to port " + PORT);

            while (!serverS.isClosed()) {
                Socket clientSocket = serverS.accept();
                System.out.println("New Connection >" + clientSocket);
                new ClientHandler(clientSocket).start();
            }

        } catch (Exception e) {
            System.out.println("Server failed");
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private SocketChannel clientSocketChannel;
        private PrintWriter out;
        private BufferedReader in;
        private String serverFilePath;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.clientSocketChannel = clientSocket.getChannel();
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.serverFilePath = "C:\\Users\\oneda\\Desktop\\OsProject\\ServerFiles\\";
        }

        public void sendFileList() {
            File files = new File(serverFilePath);
            if (files.list().length == 0) {
                out.println(1);
                out.println("No file available");

            } else {
                out.println(files.list().length);
                for (String fileName : files.list()) {
                    out.println(fileName);
                }
            }
        }

        @Override
        public void run() {
            String clientInput;
            try {
                while (((clientInput = in.readLine()) != null)) {
                    switch (clientInput) {
                        case "1":
                            sendFileList();
                            break;
                        case "ZEROCOPY":
                            String fileName1 = in.readLine();
                            File fileZ = new File(serverFilePath + fileName1);
                            if (fileZ.exists()) {
                               
                                FileChannel fc = new FileInputStream(serverFilePath + fileName1).getChannel();
                                out.println(fc.size());
                                long transferred = 0;
                                while (transferred < fc.size()) {
                                    long count = fc.transferTo(transferred,
                                            fc.size() - transferred, clientSocketChannel);
                                    transferred += count;
                                }
                            }
                            else{
                                clientSocket.close();
                            }
           
                            break;
                        case "TRADITIONAL":
                        try{
                            String fileName2 = in.readLine();
            
                            File fileT = new File(serverFilePath + fileName2);
  
                            if(fileT.exists()){
                                
                                FileInputStream fis = new FileInputStream(serverFilePath + fileName2);
                                OutputStream os = clientSocket.getOutputStream();
                                byte[] buffers = new byte[64 * 1024];
                                while (( fis.read(buffers)) != -1) {
                                    os.write(buffers);
                                }
                                os.close();
                                fis.close();
                            }
               
                        }
                        catch(Exception e){
                          
                            e.printStackTrace();
                        }
                            break;
                        default:
                            System.out.println("Wrong input from user");
                            break;
                    }
                }
            } catch (

            Exception e) {
                System.out.println("CLIENT DISCONNECT");
            }
        }
    }
}