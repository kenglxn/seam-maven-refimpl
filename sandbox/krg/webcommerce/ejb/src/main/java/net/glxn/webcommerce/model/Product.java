package net.glxn.webcommerce.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Product implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Category category;
    private Collection<File> files = new ArrayList<File>();
    private Collection<ShoppingCart> shoppingcarts = new ArrayList<ShoppingCart>();

    private static final long serialVersionUID = -8737857722150690015L;

    @Id
    @GeneratedValue
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    public Collection<File> getFiles() {
        return files;
    }

    public void setFiles(Collection<File> files) {
        this.files = files;
    }

    @Transient
    public void addFile(File file) {
        this.files.add(file);
        file.setProduct(this);
    }

    @Transient
    public void removeFile(File file) {
        this.files.remove(file);
        file.setProduct(null);
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "products")
    public Collection<ShoppingCart> getShoppingcarts() {
        return shoppingcarts;
    }

    public void setShoppingcarts(Collection<ShoppingCart> shoppingcarts) {
        this.shoppingcarts = shoppingcarts;
    }

    @Transient
    public void addShoppingcart(ShoppingCart shoppingCart) {
        this.shoppingcarts.add(shoppingCart);
    }
}
