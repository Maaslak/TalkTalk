import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;



public class CameraCapture {

    private VideoCapture camera;

    private BufferedImage image;
    private Mat frame;

    public CameraCapture() {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);
        frame = new Mat();
        camera.read(frame);

        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            while (true) {
                if (camera.read(frame)) {
                    image = MatToBufferedImage(frame);
                }
            }
        }
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

    public BufferedImage getImage() {
        return image;
    }
}
