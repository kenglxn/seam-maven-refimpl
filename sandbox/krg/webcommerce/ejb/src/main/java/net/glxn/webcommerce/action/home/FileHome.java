package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.File;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("fileHome")
public class FileHome extends EntityHome<File>
{

    @Override @Begin(join = true)
    public void create() {
        super.create();
    }


}