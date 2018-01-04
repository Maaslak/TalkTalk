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
        super.paint(g);
        if(image != null) {
            g.drawImage(image, 20, 20, this);
        }
    }
}
