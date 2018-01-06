package GUI.MainWindow;

import Audio.Microphone;
import Audio.Speakers;
import Camera.CameraCapture;
import GUI.Conference.Conference;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow {
    private JTree tree1;
    private JButton exitButton;
    private JButton connectButton;
    private JButton addContactButton;
    private JButton deleteContactButton;
    private JPanel generalPanel;
    private static CameraCapture camera;


    static private Conference conference;
    static private JFrame frame;

    public MainWindow(JFrame father) {
        frame = new JFrame("Connection to the server");
        frame.setContentPane(generalPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        camera = new CameraCapture();


        connectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                conference = new Conference(frame, camera);
                camera.setGui(conference);
                frame.setVisible(false);
            }
        });


        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.exit(0);
            }
        });
    }
}
