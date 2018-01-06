package GUI.Conference;

import Audio.Microphone;
import Audio.Speakers;
import Camera.CameraCapture;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class Conference {
    private JPanel generalPanel;
    private JPanel conferencePanel;
    private JButton discconnectButton;
    private JFrame frame;
    private JFrame father;
    private static CameraCapture camera;
    static private Microphone mic;
    static private Speakers speaker;
    static private AudioFormat format;

    public Conference(final JFrame father, CameraCapture camera) {
        this.father = father;
        frame = new JFrame("ConferencePanel");
        frame.setContentPane(generalPanel);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //camera.releaseCamera();
                mic.close();
                speaker.close();
                father.setVisible(true);
                super.windowClosing(e);
            }
        });

        this.camera = camera;
        format = new AudioFormat(44100, 16, 1, true, true);
        speaker = new Speakers(format);
        mic = new Microphone(speaker, format);

        Thread camera_thread = new Thread(camera);
        camera_thread.start();
        camera.setMyThread(camera_thread);

        Thread mic_thread = new Thread(mic);
        mic_thread.start();
    }

    public void setImage(BufferedImage image){
        ((ConferencePanel)conferencePanel).setImage(image);
        conferencePanel.repaint();
    }

    private void createUIComponents() {
        // place custom component creation code here
        conferencePanel = new ConferencePanel();

        //conferencePanel.revalidate();

    }
}
