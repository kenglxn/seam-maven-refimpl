package net.glxn.site.action.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import net.glxn.site.model.Page;
import net.glxn.site.model.File;

@Name("fileHome")
public class FileHome extends EntityHome<File>
{

    @Override @Begin(join = true)
    public void create() {
        super.create();
    }


}