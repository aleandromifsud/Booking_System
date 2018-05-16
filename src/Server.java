import java.net.ServerSocket;

public class Server
{

    public static void main(String[] args) throws Exception
    {
        ServerSocket serverSocket = new ServerSocket(555);

        System.out.println("Server Awaiting Clients");

        while (true)
        {
            new serverThreads(serverSocket.accept()).start();
        }
    }

}