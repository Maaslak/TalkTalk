package GUI.Conference;

import Audio.Microphone;
import Audio.Speakers;
import Camera.CameraCapture;
import Connection.*;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Conference implements Runnable {
    private JPanel generalPanel;
    private JPanel conferencePanel;
    private JButton discconnectButton;
    private JFrame frame;
    private JFrame father;
    private static CameraCapture camera;
    static private Microphone mic;
    static private Speakers speaker;
    static private AudioFormat format;
    private Connection connection;

    public Conference(final JFrame father, final CameraCapture camera, final Connection connection) {
        this.father = father;
        this.connection = connection;
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
        format = new AudioFormat(8000, 8, 1, true, false);
        speaker = new Speakers(format);
        mic = new Microphone(speaker, format, connection);

        Thread camera_thread = new Thread(camera);
        camera_thread.start();
        camera.setMyThread(camera_thread);

        Thread mic_thread = new Thread(mic);
        mic_thread.start();
        discconnectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Message msg = new Message();
                msg.setString("exit");
                camera.setDisconnect(true);
                try {
                    connection.write(msg);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mic.close();
                father.setVisible(true);
                //TODO Accept the disconecting
                frame.dispose();
            }
        });
    }

    public void setImage(BufferedImage image){
        ((ConferencePanel)conferencePanel).setImage(image);
        conferencePanel.repaint();
    }

    private void createUIComponents() {
        conferencePanel = new ConferencePanel();
    }

    public void run() {
        try {
            while (true) {
                Message message = connection.readMassage();
                if (message.getType() == 'i')
                    setImage(message.getImage());
                if (message.getType() == 'v')
                    speaker.play(message.getVoice(), message.getVoice().length);
                if (message.getType() == 's')
                    System.out.println(message.getString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
