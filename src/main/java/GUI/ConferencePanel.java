package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ConferencePanel extends JPanel{
    private BufferedImage image;

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}
