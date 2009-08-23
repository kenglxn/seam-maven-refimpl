package net.glxn.webcommerce.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:02:07
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Category {
    private Long id;
    private String name;
    private Category parent;
    private Collection<Product> products = new ArrayList<Product>();
    private Collection<Category> children = new ArrayList<Category>();

    @Id @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(nullable=true)
    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    public Collection<Category> getChildren() {
        return children;
    }

    public void setChildren(Collection<Category> children) {
        this.children = children;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    public Collection<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
    }
}
