package net.glxn.webcommerce.action.upload;

import org.jboss.seam.ui.graphicImage.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;


public class FileUtil implements Serializable {
    private static final long serialVersionUID = -4713978646791677279L;

    public FileUtil() {
    }

    public static byte[] getByteFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public static byte[] cropImage(byte[] byteFromFile) {
        Image image = new Image();
        try {
            image.setInput(byteFromFile);
            if (image.getHeight() > 200 || image.getWidth() > 200) {
                if (image.getHeight() > image.getWidth()) {
                    image.scaleToHeight(200);
                } else {
                    image.scaleToWidth(200);
                }
            }
            return image.getImage();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}