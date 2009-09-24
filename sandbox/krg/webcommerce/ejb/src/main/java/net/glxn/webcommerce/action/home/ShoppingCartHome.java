package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.ShoppingCart;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("shoppingCartHome")
public class ShoppingCartHome extends EntityHome<ShoppingCart> {
    private static final long serialVersionUID = 5007917347553691592L;
}