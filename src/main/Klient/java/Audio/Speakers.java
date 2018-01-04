package Audio;

import javax.sound.sampled.*;
import java.io.*;


public class Speakers implements Runnable{


    private boolean stopped;
    private byte[] data;
    private ByteArrayOutputStream audioOutputStream;

    public Speakers() {
        this.stopped = false;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void run() {
        InputStream byteArrayInputStream
                = new ByteArrayInputStream(
                data);
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        AudioInputStream audioInputStream =
                new AudioInputStream(
                        byteArrayInputStream,
                        format,
                        data.length/format.
                                getFrameSize());
        DataLine.Info dataLineInfo =
                new DataLine.Info(
                        SourceDataLine.class,
                        format);
        try {
            SourceDataLine sourceDataLine = (SourceDataLine)
                AudioSystem.getLine(
                        dataLineInfo);
            sourceDataLine.open(format);
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

}
