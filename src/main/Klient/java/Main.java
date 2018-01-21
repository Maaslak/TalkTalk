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
