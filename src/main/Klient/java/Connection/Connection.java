package Connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Connection {
    private Socket clientSocket;
    private byte[] buffer;

    public Connection() {
        try {
            clientSocket = new Socket("localhost", 1234);
            buffer = new byte[100];
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not connect to server");
        }
    }

    public void readMassage() {
        try {
            InputStream is = clientSocket.getInputStream();
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No message");
        }
    }

    public boolean disconnect() {
        try {
            clientSocket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not close connection");
        }
        return false;
    }
}
