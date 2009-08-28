package net.glxn.webcommerce.action.upload;

import net.glxn.webcommerce.action.home.FileHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import java.io.IOException;
import java.io.Serializable;
import java.io.File;


@Name("fileUpload")
public class FileUploader implements Serializable {

    @Logger
    private Log log;

    @In(create = true)
    FileHome fileHome;

    private static final long serialVersionUID = -1L;

    public void listener(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();
        log.debug("uploading file #0", item.getFile().getAbsolutePath());
        fileHome.getInstance().setImage(item.isTempFile() ? FileUtil.getByteFromFile(item.getFile()) : item.getData());
        fileHome.getInstance().setImageContentType(item.getContentType());
        fileHome.persist();
        fileHome.clearInstance();
    }
}