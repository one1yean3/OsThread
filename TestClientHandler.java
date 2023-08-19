import java.net.Socket;

public class TestClientHandler implements Runnable{
    Socket clientSocket;
    TestClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    public void run(){
        System.out.println("Test Handler "+clientSocket);
        System.out.println(Thread.currentThread().threadId());
        while(true){

        }
    }

}