package net.glxn.site.action.upload;

import net.glxn.site.action.home.FileHome;
import net.glxn.site.action.home.FileSetHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


@Name("fileUpload")
public class FileUploader implements Serializable {

    @Logger
    private Log log;

    @In(create = true)
    FileHome fileHome;

    @In(required = false)
    FileSetHome fileSetHome;

    private static final long serialVersionUID = -1L;

    public void listener(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();
        log.debug("uploading file #0", item.getFile().getAbsolutePath());
        if(fileSetHome.isManaged()) {
            log.info("fileset managed");
            fileHome.getInstance().setFileSet(fileSetHome.getInstance());
        }
        fileHome.getInstance().setImage(item.isTempFile() ? getByteFromFile(item.getFile()) : item.getData());
        fileHome.getInstance().setImageContentType(item.getContentType());
        fileHome.persist();
        fileHome.clearInstance();
    }

    private byte[] getByteFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        is.close();
        return bytes;
    }
}