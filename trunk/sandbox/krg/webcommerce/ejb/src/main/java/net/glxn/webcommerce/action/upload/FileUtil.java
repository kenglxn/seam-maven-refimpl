package net.glxn.webcommerce.action.upload;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ui.graphicImage.Image;
import org.jboss.seam.log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

@Name("fileUtil")
public class FileUtil implements Serializable {
    private static final long serialVersionUID = -4713978646791677279L;

    @Logger
    private static Log log;

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

    public static void writeToDisk(byte[] byteFromFile, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            fos.write(byteFromFile);
            fos.close();
        }
        catch (FileNotFoundException ex) {
            log.error("writeToDisk failed: {0}", ex);
        }

        catch (IOException ioe) {
            log.error("writeToDisk failed: {0}", ioe);
        }
    }
}