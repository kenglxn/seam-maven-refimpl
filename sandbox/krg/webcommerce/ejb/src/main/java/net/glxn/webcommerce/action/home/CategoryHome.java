package net.glxn.webcommerce.action.home;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;
import net.glxn.webcommerce.model.Page;
import net.glxn.webcommerce.model.Category;

@Name("categoryHome")
public class CategoryHome extends EntityHome<Category> {
    @In(required = false)
    @Out(required = false)
    Boolean setParent;

    private static final long serialVersionUID = 6855700424374076766L;
}