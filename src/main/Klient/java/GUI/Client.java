package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Client {
    private JButton exitButton;
    private JPanel generalPanel;
    private JPanel conferencePanel;
    private JFrame frame;

    public Client() {
        frame = new JFrame("ConferencePanel");
        frame.setContentPane(generalPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
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
