package net.glxn.site.action.list;

import net.glxn.site.model.Page;
import net.glxn.site.model.File;
import net.glxn.site.model.FileSet;
import net.glxn.site.action.home.PageHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;
import java.util.List;

@Name("fileSetList")
public class FileSetList extends EntityQuery<FileSet> {


    public FileSetList() {
        setEjbql("select fileSet from FileSet fileSet");
    }

}