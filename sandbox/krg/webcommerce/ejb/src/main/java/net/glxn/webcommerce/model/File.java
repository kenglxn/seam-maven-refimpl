package net.glxn.webcommerce.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 06.jul.2009
 * Time: 17:28:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class File implements Serializable {

    private static final long serialVersionUID = 6807801858063905283L;

    private Long id;
    private Integer version;
    private byte[] image;
    private String imageContentType;
    private Product product;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    private void setVersion(Integer version) {
        this.version = version;
    }

    @Lob
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}