package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.Category;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("categoryHome")
public class CategoryHome extends EntityHome<Category> {
    private static final long serialVersionUID = 6855700424374076766L;
}