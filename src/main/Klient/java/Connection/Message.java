package Connection;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class to provide an easy message to server generating
 * and parsing it to byte[]
 * It has a type variable according to the containing data type
 * There are three basic data types:
 * i - image (BufferedImage)
 * v - audio (byte[])
 * s - text (String)
 * any other type is interpreted as a text variable (String)
 */
public class Message {

    private BufferedImage image = null;
    private byte[] voice = null;
    private String string = null;
    private char type;

    /**
     * 'i' is the only type of Image message
     *
     * @param image
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        type = 'i';
    }


    /**
     * 'v' is the only type of Voice message
     * @param voice
     */
    public void setVoice(byte[] voice) {
        this.voice = voice;
        type = 'v';
    }


    /**
     * 's' is the default type of text message
     * to set different type you can change type after setting a String
     * @param string
     */
    public void setString(String string) {
        this.string = string;
        type = 's';
    }


    /**
     * There are three basic data types:
     * i - image (BufferedImage)
     * v - audio (byte[])
     * s - text (String)
     * any other type is interpreted as a text variable (String)
     * @param type
     */
    public void setType(char type) {
        this.type = type;
    }


    /**
     * Returns type of message conversion to byte[]
     * @return byte[]
     */
    public byte[] getByteType() {
        byte[] result = new byte[1];
        result[0] = (byte) type;
        return result;
    }


    /**
     * Returns a type of message presented as a char
     * @return char (type)
     */
    public char getType() {
        return type;
    }

    /**
     * Converting a byte table to the message
     * Interpreting depends on message type
     * @param bytes
     */
    public void updateMessage(byte[] bytes) {
        if (type == 'i') {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try {
                image = ImageIO.read(bais);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type == 'v')
            voice = bytes;
        else {
            string = new String(bytes);
        }

    }

    /**
     * Private function which converts BufferedImage to bytes[]
     * @return
     */
    private byte[] imageToBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageOutputStream outputStream = ImageIO.createImageOutputStream(baos);
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();


            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.5f);

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, new IIOImage(image, null, null), jpgWriteParam);

            //ImageIO.write(image, "jpg", baos);
            baos.flush();
            jpgWriter.dispose();
            byte[] bytes = baos.toByteArray();
            baos.close();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Converts content of message to bytes[]
     * @return bytes[]
     */
    public byte[] toBytes() {
        if (type == 'i')
            return imageToBytes();
        if (type == 'v')
            return voice;
        return string.getBytes();
    }

    public void setBytes(byte[] bytes) {

    }

    public BufferedImage getImage() {
        return image;
    }

    public byte[] getVoice() {
        return voice;
    }

    public String getString() {
        return string;
    }
}
