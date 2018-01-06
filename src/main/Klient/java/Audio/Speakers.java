package Audio;

import javax.sound.sampled.*;
import java.io.*;


public class Speakers {

    private DataLine.Info info;
    private SourceDataLine speaker;
    private AudioFormat format;

    public Speakers(AudioFormat format) {
        this.format = format;
        info = new DataLine.Info(SourceDataLine.class, format);
        try {
            speaker = (SourceDataLine) AudioSystem.getLine((info));
            speaker.open(format);
            speaker.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void play(byte[] data, int numBytesRead) {
        speaker.write(data, 0, numBytesRead);
    }
}
