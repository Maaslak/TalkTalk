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


    public Connection(String ip, String username, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        inputBuffer = new byte[4];
        Message usernameMessage = new Message();
        usernameMessage.setString(username);
        write(usernameMessage);
        Message msg = readMassage();
        msg.updateMessage(inputBuffer);
        String response = msg.getString();
        if (!response.equals("ok"))
            throw new IOException("Serwer busy");

    }

    public Message readMassage() throws IOException {
        Message msg = new Message();
        InputStream inputStream = clientSocket.getInputStream();
        inputBuffer = new byte[1];
        inputStream.read(inputBuffer, 0, 1);
        char type = (char) inputBuffer[0];
        msg.setType(type);
        inputBuffer = new byte[4];
        inputStream.read(inputBuffer, 0, 4);
        int size = ByteBuffer.wrap(inputBuffer).asIntBuffer().get();
        inputBuffer = new byte[size];
        inputStream.read(inputBuffer, 0, size);
        msg.updateMessage(inputBuffer);
        return msg;
    }

    public Message readIncommingConnetionMassage() throws Exception {
        Message msg = new Message();
        InputStream inputStream = clientSocket.getInputStream();
        inputBuffer = new byte[1];
        int res = inputStream.read(inputBuffer, 0, inputStream.available());
        if (res == -1) throw new Exception("not yet");
        char type = (char) inputBuffer[0];
        msg.setType(type);
        inputBuffer = new byte[4];
        inputStream.read(inputBuffer, 0, 4);
        int size = ByteBuffer.wrap(inputBuffer).asIntBuffer().get();
        inputBuffer = new byte[size];
        inputStream.read(inputBuffer, 0, size);
        msg.updateMessage(inputBuffer);
        return msg;
    }

    public byte[] getInputBuffer() {
        return inputBuffer;
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
