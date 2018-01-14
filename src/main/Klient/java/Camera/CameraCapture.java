package Camera;

import Connection.Connection;
import Connection.Message;
import GUI.Conference.Conference;
import org.opencv.core.Mat;
import org.opencv.highgui.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.rmi.ConnectException;


public class CameraCapture implements Runnable{

    private VideoCapture camera;

    private BufferedImage image;

    private Mat frame;

    private Conference gui;

    private Thread myThread;

    private boolean disconnect = false;

    private Connection connection;


    public CameraCapture(Connection connection) {
        this.connection = connection;
        initCamera();
    }

    public void setGui(Conference gui) {
        this.gui = gui;
    }

    /**Initialize camera
     */
    public void initCamera(){
        camera = new VideoCapture(0);

        frame = new Mat();
        //camera.read(frame);

        if (!camera.isOpened())
            System.out.println("Error");
    }

    public void releaseCamera(){
        disconnect = true;
    }

    public void openCamera(){
        camera.open(0);
    }

    public void setMyThread(Thread myThread) {
        this.myThread = myThread;
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

    public void setDisconnect(boolean disconnect) {
        this.disconnect = disconnect;
    }

    public void run() {
        while (!disconnect) {
            readImage();
            Message cameramsg = new Message();
            cameramsg.setImage(image);

            try {
                connection.write(cameramsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //gui.setImage(image);
        }

    }
}
