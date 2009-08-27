package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("productList")
public class ProductList extends EntityQuery<Product> {
    private static final long serialVersionUID = -3444306007404323006L;

    public ProductList() {
        setEjbql("select product from Product product");
    }

}