package Camera;

import GUI.Client;
import org.opencv.core.Mat;
import org.opencv.highgui.*;
import sun.awt.Mutex;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;



public class CameraCapture implements Runnable{

    private VideoCapture camera;

    private BufferedImage image;

    private Mat frame;

    private Client gui;



    public CameraCapture(Client gui) {
        this.gui = gui;
        nu.pattern.OpenCV.loadLibrary();
        initCamera();
    }

    /**Initialize camera
     */
    public void initCamera(){
        camera = new VideoCapture(0);

        frame = new Mat();
        camera.read(frame);

        if (!camera.isOpened())
            System.out.println("Error");
    }

    public void releaseCamera(){
        camera.release();
    }

    public void openCamera(){
        camera.open(0);
    }


    /**
     * Reads camera image
     */
    private void readImage(){
        if (camera.read(frame)) {
            image = MatToBufferedImage(frame);
        }
        else
            System.out.println("Couldnt read camera input");
    }

    /** Converts Mat to BufferedImage
     * @param frame
     * @return image
     */
    private BufferedImage MatToBufferedImage(Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }


    public void run() {
        while (true){
            readImage();
            gui.setImage(image);
        }
    }
}
