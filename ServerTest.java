import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
// import java.io.BufferedWriter;
// import java.io.File;
import java.io.IOException;
// import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;

public class ServerTest{

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(25404);
        Socket client;
        // serverSocket.setReuseAddress(true);
        while(!serverSocket.isClosed()) {
            try{
                client = serverSocket.accept();
                System.out.println("New client connected \nIP : " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }catch(IOException e){
                e.printStackTrace();
            }  
        }   
        serverSocket.close();
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }
        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;
            FileList f = new FileList();
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                String line;
                while ((line = in.readLine()) != null) {
                    //Check if client input String equal File name in server database
                    
                    if(f.searchFile(line)){
                        sendFile(line);
                        System.out.println("Download "+line);
                        out.println("you can download");

                    }
                    else if(line.equalsIgnoreCase("exit")){
                        System.out.println("Client disconnected \nIP : " + clientSocket.getInetAddress().getHostAddress());
                        System.out.println(Thread.currentThread().getName());
                        out.println("Bye bye");
                    }
                    else{
                        System.out.printf("Sent from the client: %s\n",line);
                        out.println(f.printAllFileString());
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void sendFile(String fileName) {
            try {
                //handle file read
                File myFile = new File(fileName);
                byte[] mybytearray = new byte[(int) myFile.length()];

                FileInputStream fis = new FileInputStream(myFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                //bis.read(mybytearray, 0, mybytearray.length);

                DataInputStream dis = new DataInputStream(bis);
                dis.readFully(mybytearray, 0, mybytearray.length);

                //handle file send over socket
                OutputStream os = clientSocket.getOutputStream();

                //Sending file name and file size to the server
                DataOutputStream dos = new DataOutputStream(os);
                dos.writeUTF(myFile.getName());
                dos.writeLong(mybytearray.length);
                dos.write(mybytearray, 0, mybytearray.length);
                dos.flush();
                System.out.println("File "+fileName+" sent to client.");
            } 
            catch (Exception e) {
                System.err.println("File does not exist!");
            } 
        }
    }

}
