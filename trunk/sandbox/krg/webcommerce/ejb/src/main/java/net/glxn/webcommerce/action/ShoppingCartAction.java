package net.glxn.webcommerce.action;

import net.glxn.webcommerce.action.home.ShoppingCartHome;
import net.glxn.webcommerce.model.Product;
import net.glxn.webcommerce.model.ShoppingCart;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;

@Name("shoppingCartAction")
public class ShoppingCartAction {

    @Logger
    Log log;

    @In(create = true)
    ShoppingCartHome shoppingCartHome;

    @In(required = false)
    @Out(required = false, scope = ScopeType.SESSION)
    ShoppingCart userShoppingCart;

    @Factory("userShoppingCart")
    public void initUserShoppingCart() {
        log.info("initializing userShoppingCart");
        userShoppingCart = new ShoppingCart();
    }

    public void addProductToCart(Product product) {
        log.info("adding #0 to cart", product.getName());
        userShoppingCart.addProduct(product);        
    }
}
