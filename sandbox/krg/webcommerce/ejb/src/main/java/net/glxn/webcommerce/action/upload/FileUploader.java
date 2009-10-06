package net.glxn.webcommerce.action.upload;

import net.glxn.webcommerce.action.home.FileHome;
import net.glxn.webcommerce.action.home.PageHome;
import net.glxn.webcommerce.action.home.ProductHome;
import net.glxn.webcommerce.action.list.SettingsList;
import net.glxn.webcommerce.model.File;
import net.glxn.webcommerce.model.ImageByte;
import net.glxn.webcommerce.model.Settings;
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

    @In(create = true)
    SettingsList settingsList;

    @In(required = false)
    PageHome pageHome;
    private static final long serialVersionUID = -5955269352412127177L;


    public void listener(UploadEvent event) throws IOException {
        UploadItem item = event.getUploadItem();
        String fileName = item.getFileName();
        log.info("uploading file #0", fileName);
        fileHome.clearInstance();
        File file = fileHome.getInstance();
        byte[] byteFromFile = FileUtil.getByteFromFile(item.getFile());
        file.setOriginalByte(new ImageByte(byteFromFile));
        byte[] croppedImage = FileUtil.cropImage(byteFromFile);
        Settings settings = settingsList.getSingleResult();
        String filepath = settings.getFilePathServer();
        FileUtil.writeToDisk(byteFromFile, filepath.concat(fileName));
        file.setFileName(fileName);        
        file.setCroppedByte(new ImageByte(croppedImage));
        file.setImageContentType(item.getContentType());
        if (productHome != null && productHome.isManaged()) {
            productHome.getInstance().addFile(file);
        }
        if (pageHome != null && pageHome.isManaged()) {
            pageHome.getInstance().addFile(file);
        }
        fileHome.persist();
    }
}