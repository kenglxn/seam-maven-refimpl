package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.Page;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;

@Name("pageHome")
public class PageHome extends EntityHome<Page> {
    @In(required = false)
    @Out(required = false)
    Boolean setParent;

    private static final long serialVersionUID = 5673441719051773010L;
}