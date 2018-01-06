import Audio.Microphone;
import Audio.Speakers;
import Camera.CameraCapture;
import GUI.Conference.Conference;

import javax.sound.sampled.AudioFormat;

public class Main {

    private static CameraCapture camera;
    static private Microphone mic;
    static private Conference gui;
    static private Speakers speaker;
    static private AudioFormat format;

    public static void main(String[] args) {
        gui = new Conference();
        camera = new CameraCapture(gui);
        format = new AudioFormat(44100, 16, 1, true, true);
        speaker = new Speakers(format);
        mic = new Microphone(speaker, format);

        Thread camera_thread = new Thread(camera);
        camera_thread.start();

        Thread mic_thread = new Thread(mic);
        mic_thread.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //mic.start();
        //speaker.start();
    }
}
