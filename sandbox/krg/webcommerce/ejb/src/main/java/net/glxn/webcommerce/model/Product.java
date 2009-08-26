package net.glxn.webcommerce.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:03:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Product {
    private Long id;
    private Category category;
    private File product;

    @Id @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public File getProduct() {
        return product;
    }

    public void setProduct(File product) {
        this.product = product;
    }
}
