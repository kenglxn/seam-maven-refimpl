package net.glxn.webcommerce.action.upload;

import net.glxn.webcommerce.action.home.FileHome;
import net.glxn.webcommerce.action.home.ProductHome;
import net.glxn.webcommerce.model.File;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import java.io.IOException;
import java.io.Serializable;


@Name("fileUpload")
public class FileUploader implements Serializable {

    @Logger
    private Log log;

    @In(create = true)
    FileHome fileHome;

    @In(required = false)
    ProductHome productHome;

    private static final long serialVersionUID = -1L;

    public void listener(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();
        log.debug("uploading file #0", item.getFile().getAbsolutePath());
        fileHome.clearInstance();
        File file = fileHome.getInstance();
        file.setImage(item.isTempFile() ? FileUtil.getByteFromFile(item.getFile()) : item.getData());
        file.setImageContentType(item.getContentType());
        if(productHome.isManaged()) {
            productHome.getInstance().addFile(file);
            file.setProduct(productHome.getInstance());
        }
        fileHome.persist();
    }
}