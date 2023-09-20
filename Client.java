import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to server...");
            Socket clientSocket = new Socket("localhost", 55555);
            System.out.println("Connect successful");

            Scanner sc = new Scanner(System.in);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while (!clientSocket.isClosed()) {
                printChoice();
                line = sc.nextLine();
                switch (line) {
                    case "exit":
                        System.out.println("Bye bye!");
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
                        out.println(line);

                        System.out.println("Please input file name :");
                        String filename = sc.nextLine();
                        out.println(filename);
                        if(in.readLine().equalsIgnoreCase("yes")){

                        
                        long fileSize = Long.parseLong(in.readLine());
                        System.out.println("Size = " + fileSize);
                        for (int i = 0; i < 10; i++) {
                            long start = (i * fileSize) / 10;
                            long end = ((i + 1) * fileSize) / 10;
                            out.println(start);
                            out.println(end);
                            Thread t = new DownloadHandler(clientSocket, start, end, filename);
                            t.start();
                            






















                            Thread.sleep(100);
                        }
                        }
                        else{
                            System.out.println("No file found");
                        }
                        break;
                    default:
                        System.out.println("Wrong input");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Connection error");
        }

    }

    public static void printChoice() {
        System.out.println("Type 1 to see all files");
        System.out.println("Type 2 to choose a file to download");
        System.out.println("Type exit to exit the program");
    }

    public static class DownloadHandler extends Thread {
        private long start;
        private long end;
        private Socket client;
        private String file;

        public DownloadHandler(Socket client, long start, long end, String fileName) throws IOException {
            this.client = client;
            this.start = start;
            this.end = end;
            this.file = fileName;
        }

        public void run() {
            try {
                byte[] buffer = new byte[10240];
                RandomAccessFile raf = new RandomAccessFile(".\\Downloaded\\"+file, "rw");
                int bytesRead;

                InputStream in2 = client.getInputStream();
                raf.seek(start);
                while (start < end && (bytesRead = in2.read(buffer)) != -1) {
                    raf.write(buffer);
                    start += bytesRead;
                }

                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
