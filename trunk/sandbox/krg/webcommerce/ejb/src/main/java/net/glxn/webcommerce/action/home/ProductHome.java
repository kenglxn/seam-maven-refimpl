package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityHome;

@Name("productHome")
public class ProductHome extends EntityHome<Product> {
    @In(required = false)
    @Out(required = false)
    Boolean setCategory;

    private static final long serialVersionUID = 447462015482288188L;
}