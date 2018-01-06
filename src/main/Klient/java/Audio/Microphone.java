package Audio;

import sun.awt.Mutex;

import javax.sound.sampled.*;


public class Microphone implements Runnable {
    private AudioFormat format;
    private TargetDataLine microphone;

    private byte[] data;
    private int numBytesRead;
    private boolean stopped;
    private Speakers speaker;
    private Mutex dataMutex;
    private Mutex numBytesMutex;

    public Microphone(Speakers speaker, AudioFormat format) {
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
        stopped = false;
    }

    public void start() {
        microphone.start();
        AudioInputStream audio_input_stream = new AudioInputStream(microphone);
        while(!stopped) {
            numBytesMutex.lock();
            dataMutex.lock();
            numBytesRead = microphone.read(data, 0, data.length);
            dataMutex.unlock();
            numBytesMutex.unlock();

            speaker.play(data, numBytesRead);
        }
        microphone.stop();
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
