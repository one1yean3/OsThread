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
                        case "2":
                            String fileName = in.readLine();
                            File file = new File(serverFilePath + fileName);
                            if (file.exists()) {
                                out.println(file.length());
                            } else {
                                out.println(-1);
                            }
                            break;
                        case "3":
                            String fileName2 = in.readLine();
                            File file2 = new File(serverFilePath + fileName2);

                            if (file2.exists()) {
                                FileChannel fc = new FileInputStream(serverFilePath + fileName2).getChannel();
                                System.out.println(file2.length());
                                long fileS = file2.length();
                                System.out.println(fc.size());
                                out.println(fc.size());

                                long totalBytesTransferred = 0;
                                while (totalBytesTransferred < fileS) {
                                    long bytesTransferred = fc.transferTo(totalBytesTransferred,
                                            fc.size() - totalBytesTransferred, clientSocketChannel);
                                    System.out.println(bytesTransferred);
                                    totalBytesTransferred += bytesTransferred;
                                }
                            }
                            break;
                        case "4":
                            String fileName3 = in.readLine();
                           
                            FileInputStream file3 = new FileInputStream(serverFilePath + fileName3);
                            

                            OutputStream os1 = clientSocket.getOutputStream();
                            byte[] buffers = new byte[64 * 1024];
                            while ( file3.read(buffers) != -1) {
                                os1.write(buffers);
                            }
                            os1.close();
                            file3.close();
                            break;
                        case "DOWNLOAD":

                            String fileN = in.readLine();
                            long start = Long.parseLong(in.readLine());
                            long end = Long.parseLong(in.readLine());

                            RandomAccessFile raf = new RandomAccessFile(new File(serverFilePath + fileN), "r");

                            byte[] buffer = new byte[64 * 1024];
                            int bytesRead;

                            raf.seek(start);

                            OutputStream os = clientSocket.getOutputStream();

                            while (start < end && (bytesRead = raf.read(buffer)) != -1) {
                                os.write(buffer);
                                start += bytesRead;
                            }
                            raf.close();
                            os.close();
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