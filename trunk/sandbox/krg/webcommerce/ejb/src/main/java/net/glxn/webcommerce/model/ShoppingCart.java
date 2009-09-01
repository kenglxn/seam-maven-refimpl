package net.glxn.webcommerce.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:03:45
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class ShoppingCart implements Serializable {

    private Long id;
    private User customer;
    private Collection<Product> products = new ArrayList<Product>();

    private static final long serialVersionUID = 1130567133942358409L;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }


    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}
    )
    @JoinTable(
            name = "Shoppingcart_Products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    public Collection<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
    }

    @Transient
    public void addProduct(Product product) {
        this.products.add(product);
        product.addShoppingcart(this);
    }
}
