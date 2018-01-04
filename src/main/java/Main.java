import Camera.CameraCapture;
import GUI.Client;

import java.awt.image.BufferedImage;

public class Main {
    private static CameraCapture camera;
    public static void main(String[] args) {
        Client gui = new Client();
        camera = new CameraCapture();
        while (true){
            BufferedImage temp = camera.getImage();
            gui.setImage(temp);
        }
    }
}
