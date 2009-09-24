package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.ShoppingCart;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("shoppingCartList")
public class ShoppingCartList extends EntityQuery<ShoppingCart> {

    private static final long serialVersionUID = 6303768144867580307L;

    public ShoppingCartList() {
        setEjbql("select sc from ShoppingCart sc");
    }

}