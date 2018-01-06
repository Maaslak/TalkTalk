package Connection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Message {
    BufferedImage image;
    boolean disconnect;

    public Message(BufferedImage image, boolean disconnect) {
        this.image = image;
        this.disconnect = disconnect;
    }

    public byte[] getBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            baos.flush();

            byte[] imageInByte = baos.toByteArray();
            byte disconnectInByte = (byte) (disconnect ? 1 : 0);

            byte[] bytes = new byte[imageInByte.length + 1]; //result

            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = i < imageInByte.length ? imageInByte[i] : disconnectInByte;
            }
            baos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setBytes(byte[] bytes) {

    }
}
