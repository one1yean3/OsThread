import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            String serverIP = "192.168.1.19";
            System.out.println("Connecting to server...");
            SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 55555));
            Socket clientSocket = socketChannel.socket();
            System.out.println("Connect successful");

            Scanner sc = new Scanner(System.in);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            InputStream in2 = clientSocket.getInputStream();
            String line;
            while (!clientSocket.isClosed()) {
                printChoice();
                line = sc.nextLine();
                switch (line) {
                    case "exit":
                        System.out.println("Bye bye!");
                        sc.close();
                        clientSocket.close();
                        break;
                    case "1":
                        out.println(line);
                        int size = Integer.parseInt(in.readLine());
                        for (int i = 0; i < size; i++) {
                            System.out.println(in.readLine());
                        }
                        break;
                    case "2":
                        System.out.println("MULTITHREAD");
                        out.println(line);
                        System.out.println("Please input file name :");
                        String filename = sc.nextLine();
                        out.println(filename);

                        long fileSize = Long.parseLong(in.readLine());
                        System.out.println(fileSize);
                        int threadNumbs = 10;
                        if (fileSize != -1) {
                            for (int i = 0; i < threadNumbs; i++) {
                                long start = (i * fileSize) / threadNumbs;
                                long end = ((i + 1) * fileSize) / threadNumbs;
                                Socket clientSocket2 = new Socket(serverIP, 55555);
                                new Downloader(clientSocket2, start, end, filename).start();
                            }
                        } else {
                            System.out.println("invalid file");
                        }
                        break;
                    case "3":
                        System.out.println("ZERO COPY");
                        out.println(line);
                        System.out.println("Please input file name :");
                        String filename2 = sc.nextLine();
                        out.println(filename2);
                        long fileSize2 = Long.parseLong(in.readLine());
                        System.out.println("SIZE " + fileSize2);
                        FileOutputStream fileOutputStream = new FileOutputStream(".//" + filename2);
                        FileChannel fileChannel = fileOutputStream.getChannel();
                        long totalBytesTransferFrom = 0;
                        long start = System.currentTimeMillis();
                        while (totalBytesTransferFrom < fileSize2) {
                            long transferFromByteCount = fileChannel.transferFrom(socketChannel, totalBytesTransferFrom,
                                    fileSize2 - totalBytesTransferFrom);
                            if (transferFromByteCount <= 0) {

                                break;
                            }
                            totalBytesTransferFrom += transferFromByteCount;
                        }
                        long end = System.currentTimeMillis();
                        long time = end - start;
                        System.out.println("TIME USED : " + time);
                        fileChannel.close();
                        fileOutputStream.close();

                        // System.out.println("invalid file");
                        // }
                        break;
                    case "4":
                        System.out.println("TRADITIONAL");
                        out.println(line);

                        String fileName4 = sc.nextLine();
                        out.println(fileName4);


                        byte[] buffer = new byte[64 * 1024];
                        FileOutputStream raf = new FileOutputStream(".//" + fileName4);
                  
                        
                        
                        long start2 = System.currentTimeMillis();
                        while (in2.read(buffer) != -1) {
                            raf.write(buffer);
                        
                        }
                        raf.close();
                        
                        long end2 = System.currentTimeMillis();
                        long time2 = end2 - start2;
                        System.out.println("TIME USED : " + time2);
                        break;
                    default:
                        System.out.println("Wrong input");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection error");
        }

    }

    public static void printChoice() {
        System.out.println("Type 1 to see all files");
        System.out.println("Type 2 to choose a file to download");
        System.out.println("Type exit to exit the program");
    }

    public static class Downloader extends Thread {
        private long start;
        private long end;
        private Socket client;
        private String fileName;

        public Downloader(Socket client, long start, long end, String fileName) throws IOException {
            this.client = client;
            this.start = start;
            this.end = end;
            this.fileName = fileName;

        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("DOWNLOAD");
                out.println(fileName);
                out.println(start);
                out.println(end);

                byte[] buffer = new byte[64 * 1024];
                RandomAccessFile raf = new RandomAccessFile(".//" + fileName, "rw");
                int bytesRead;
                InputStream in = client.getInputStream();
                while (start < end && (bytesRead = in.read(buffer)) != -1) {
                    raf.seek(start);
                    raf.write(buffer);
                    start += bytesRead;
                }
                raf.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
