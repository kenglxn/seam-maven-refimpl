package net.glxn.site.action.list;

import net.glxn.site.action.home.FileSetHome;
import net.glxn.site.model.File;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;
import java.util.List;

@Name("fileList")
public class FileList extends EntityQuery<File> {

    @In
    EntityManager entityManager;

    @In(required = false)
    FileSetHome fileSetHome;

    public FileList() {
        setEjbql("select file from File file");
    }

    public List<File> getFilesForFileSet() {
        return entityManager.createQuery("select file from File file where file.fileSet.id =:fileSetId ")
                .setParameter("fileSetId", fileSetHome.getId())
                .getResultList();
    }

    public List<File> getFilesWithoutFilesets() {
        return entityManager.createQuery("select file from File file where file.fileSet is null")
                .getResultList();
    }
}