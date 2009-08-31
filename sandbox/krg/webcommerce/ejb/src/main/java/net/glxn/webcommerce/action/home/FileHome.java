package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.File;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

@Name("fileHome")
public class FileHome extends EntityHome<File> {
    private static final long serialVersionUID = 3932216405937924123L;
}