import Camera.CameraCapture;
import GUI.Client;

import java.awt.image.BufferedImage;

public class Main {

    private static CameraCapture camera;
    public static void main(String[] args) {
        Client gui = new Client();
        camera = new CameraCapture(gui);
        Thread camera_thread = new Thread(camera);
        camera_thread.start();

    }
}
