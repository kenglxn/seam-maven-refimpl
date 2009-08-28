package net.glxn.webcommerce.action.upload;

import java.io.*;

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
}