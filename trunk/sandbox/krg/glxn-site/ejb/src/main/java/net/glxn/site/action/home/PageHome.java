package net.glxn.site.action.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import net.glxn.site.model.Page;

@Name("pageHome")
public class PageHome extends EntityHome<Page>
{
    @In(required = false)
    @Out(required = false)
    Boolean setParent;

    @In(required = false)
    @Out(required = false)
    Boolean addFileSet;
    
}