package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.File;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;

@Name("fileList")
public class FileList extends EntityQuery<File> {

    @In
    EntityManager entityManager;

    public FileList() {
        setEjbql("select file from File file");
    }
}