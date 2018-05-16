import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class serverThreads extends Thread{

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Socket clientConnection;


    public serverThreads(Socket clientConnection) throws IOException
    {
        this.clientConnection = clientConnection;
        this.outputStream = new ObjectOutputStream(clientConnection.getOutputStream());
        this.inputStream = new ObjectInputStream(clientConnection.getInputStream());
    }

    //private

    @Override
    public void run()
    {
        System.out.println("Client Has Connected");
    }

}
