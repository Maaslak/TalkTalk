package Connection;

import sun.awt.Mutex;

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
    private Mutex mux;
    private boolean connected;

    public Connection(String ip, String username, int port) throws IOException {
        connected = false;
        mux = new Mutex();
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
        int size = bytesToLen(inputBuffer);//ByteBuffer.wrap(inputBuffer).asIntBuffer().get();
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

        new Thread() {
            @Override
            public void run() {
                /*
                try {
                    while (!connected) {

                        readMassage();
                        if(msg.getType() == 's'){

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Read message exception");
                }
                */

            }
        }.start();

    }

    private byte[] lenToBytes(int value) {
        byte[] result = new byte[4];
        int i = 3;
        while (i >= 0) {
            result[i] = (byte) (value % 256);
            i--;
            value /= 256;
        }
        return result;
    }

    private int bytesToLen(byte[] bytes) {
        int result = 0;
        int base = 1;
        for (int i = 3; i >= 0; i--) {
            result += base * (int) (bytes[i]);
            base *= 256;
        }
        return result;
    }

    public void write(Message msg) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        byte[] byteType = msg.getByteType();
        outputStream.write(byteType, 0, 1);
        byte[] bytes = msg.toBytes();
        byte[] byteBuffer = lenToBytes(bytes.length);
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
