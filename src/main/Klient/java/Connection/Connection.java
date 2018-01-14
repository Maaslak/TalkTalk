package Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Connection {
    private Socket clientSocket;
    private byte[] inputBuffer;
    private byte[] outputBuffer;
    private String username;
    private boolean isEstablished = false;
    private Message msg;

    public Connection(String ip, String username, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        inputBuffer = new byte[4];
        msg = new Message();
        Message usernameMessage = new Message();
        usernameMessage.setString(username);
        write(usernameMessage);
        Message msg = readMassage();
        msg.updateMessage(inputBuffer);
        String response = msg.getString();
        if (response.equals("ok"))
            isEstablished = true;
        else throw new IOException("Serwer busy");
    }

    public Message readMassage() throws IOException {
        InputStream inputStream = clientSocket.getInputStream();
        inputBuffer = new byte[1];
        inputStream.read(inputBuffer, 0, 1);
        char type = (char) inputBuffer[0];
        this.msg.setType(type);
        inputBuffer = new byte[4];
        inputStream.read(inputBuffer, 0, 4);
        int size = ByteBuffer.wrap(inputBuffer).asIntBuffer().get();
        inputBuffer = new byte[size];
        inputStream.read(inputBuffer, 0, size);
        this.msg.updateMessage(inputBuffer);
        return this.msg;
    }

    public boolean isEstablished() {
        return isEstablished;
    }

    public byte[] getInputBuffer() {
        return inputBuffer;
    }

    private void read() {
        /*
        new Thread() {
            @Override
            public void run() {
                try {
                    while (!clientSocket.isClosed()) {
                        readMassage();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Read message exception");
                }
            }
        }.start();
        */
    }

    public void write(Message msg) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        byte[] byteType = msg.getByteType();
        byte[] bytes = msg.toBytes();
        byte[] byteBuffer = ByteBuffer.allocate(4).putInt(bytes.length).array();
        outputStream.write(byteType, 0, 1);
        outputStream.write(byteBuffer, 0, 4);
        outputStream.write(bytes, 0, bytes.length);
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
