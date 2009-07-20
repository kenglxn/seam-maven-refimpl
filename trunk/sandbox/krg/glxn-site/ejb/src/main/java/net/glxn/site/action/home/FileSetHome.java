package net.glxn.site.action.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import net.glxn.site.model.Page;
import net.glxn.site.model.File;
import net.glxn.site.model.FileSet;

@Name("fileSetHome")
public class FileSetHome extends EntityHome<FileSet>
{

    @RequestParameter
    Long fileSetId;

    @Override
    public Object getId()
    {
        if (fileSetId == null)
        {
            return super.getId();
        }
        else
        {
            return fileSetId;
        }
    }

    @Override @Begin(join = true)
    public void create() {
        super.create();
    }




}