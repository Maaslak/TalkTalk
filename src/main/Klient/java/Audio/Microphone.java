package Audio;

import Connection.Connection;
import Connection.Message;
import GUI.Connect.Connect;
import sun.awt.Mutex;
import sun.awt.windows.ThemeReader;

import javax.sound.sampled.*;
import java.io.IOException;


public class Microphone implements Runnable {
    private AudioFormat format;
    private TargetDataLine microphone;

    private byte[] data;
    private int numBytesRead;
    private boolean closed;
    private Speakers speaker;
    private Mutex dataMutex;
    private Mutex numBytesMutex;
    private Mutex writeMux;
    private Connection connection;

    public Microphone(Speakers speaker, AudioFormat format, Connection connection, Mutex mux) {
        this.writeMux = mux;
        this.connection = connection;
        this.speaker = speaker;
        //out = new ByteArrayOutputStream()
        this.format = format;
        dataMutex = new Mutex();
        numBytesMutex = new Mutex();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            if (!AudioSystem.isLineSupported(info)) throw new LineUnavailableException();
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        data = new byte[microphone.getBufferSize() / 5];
        closed = false;
    }

    public void start() {
        microphone.start();
        AudioInputStream audio_input_stream = new AudioInputStream(microphone);
        while (!closed) {
            numBytesMutex.lock();
            dataMutex.lock();
            numBytesRead = microphone.read(data, 0, data.length);
            dataMutex.unlock();
            numBytesMutex.unlock();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message audio = new Message();
            audio.setVoice(data);

            try {
                writeMux.lock();
                connection.write(audio);
                writeMux.unlock();
            } catch (IOException e) {
                e.printStackTrace();
                if(e.getMessage().equals("Disconnected"))
                    System.exit(-1);
            }

        }
        microphone.close();
    }

    public void close() {
        closed = true;
    }

    public byte[] getData() {
        byte[] temp;
        dataMutex.lock();
        temp = data.clone();
        dataMutex.unlock();
        return temp;
    }

    public int getNumBytesRead() {
        int temp;
        numBytesMutex.lock();
        temp = numBytesRead;
        numBytesMutex.unlock();
        return numBytesRead;
    }

    public void run() {
        this.start();
    }
}
