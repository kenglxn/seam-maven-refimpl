package net.glxn.webcommerce.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:03:03
 * To change this template use File | Settings | File Templates.
 */
@Cache(usage= CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Product implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Category category;
    private Collection<File> files = new ArrayList<File>();

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
}
