package net.glxn.webcommerce.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:03:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Product {
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private Long id;

    @Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
