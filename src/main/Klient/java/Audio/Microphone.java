package Audio;

import javax.sound.sampled.*;


public class Microphone {
    private AudioFormat format;
    private TargetDataLine microphone;

    private byte[] data;
    private int numBytesRead;
    private boolean stopped;
    private Speakers speaker;

    public Microphone(Speakers speaker) {
        this.speaker = speaker;
        //out = new ByteArrayOutputStream()
        format = new AudioFormat(8000.0f, 16, 1, true, true);
        try {
            microphone = AudioSystem.getTargetDataLine(format);
            microphone.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        data = new byte[microphone.getBufferSize() / 5];
        stopped = false;
    }

    public void start() {
        microphone.start();

        System.out.println("Start capturing");
        AudioInputStream audio_input_stream = new AudioInputStream(microphone);
        System.out.println("Start recording");
        while(!stopped) {
            numBytesRead = microphone.read(data, 0, data.length);
            speaker.setData(data);
        }
        microphone.stop();

        System.out.println("Number of bytes: " + numBytesRead);
        System.out.println("Finished");

    }
}
