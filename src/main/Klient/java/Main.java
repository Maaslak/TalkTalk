import Audio.Microphone;
import Audio.Speakers;
import Camera.CameraCapture;
import Connection.Connection;
import GUI.Conference.Conference;
import GUI.Connect.Connect;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

public class Main {



    public static void main(String[] args) {

        byte[] byteBuffer = ByteBuffer.allocate(4).putInt(254).array();
        int size = ByteBuffer.wrap(byteBuffer).asIntBuffer().get();
        System.out.println(size);
        nu.pattern.OpenCV.loadLibrary();
        Connect con = new Connect();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //mic.start();
        //speaker.start();
    }
}
