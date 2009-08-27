package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.File;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("fileList")
public class FileList extends EntityQuery<File> {
    private static final long serialVersionUID = -3406620263605736125L;

    public FileList() {
        setEjbql("select file from File file");
    }
}