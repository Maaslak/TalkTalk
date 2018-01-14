package Connection;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Message {

    private BufferedImage image = null;
    private byte[] voice = null;
    private String string = null;
    private char type;

    public Message() {
    }

    //public void clear

    public void setImage(BufferedImage image) {
        this.image = image;
        type = 'i';
    }

    public void setVoice(byte[] voice) {
        this.voice = voice;
        type = 'v';
    }

    public void setString(String string) {
        this.string = string;
        type = 's';
    }

    public void setType(char type) {
        this.type = type;
    }

    public byte[] getByteType() {
        byte[] result = new byte[1];
        result[0] = (byte) type;
        return result;
    }

    public char getType() {
        return type;
    }

    public void updateMessage(byte[] bytes) {
        if (type == 's')
            string = new String(bytes);
        else if (type == 'v')
            voice = bytes;
        else {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try {
                image = ImageIO.read(bais);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

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

    public byte[] toBytes() {
        if (image != null)
            return imageToBytes();
        if (voice != null)
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
