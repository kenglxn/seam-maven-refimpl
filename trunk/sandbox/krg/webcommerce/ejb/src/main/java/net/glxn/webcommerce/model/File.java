package net.glxn.webcommerce.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
@Table
public class File implements Serializable {

    private static final long serialVersionUID = 6807801858063905283L;

    private Long id;
    private Integer version;
    private byte[] image;
    private String imageContentType;
    private Product product;
    private Page page;
    private byte[] originalImage;

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

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    private ImageByte originalByte;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public ImageByte getOriginalByte() {
        return originalByte;
    }

    public void setOriginalByte(ImageByte originalByte) {
        this.originalByte = originalByte;
    }

    private ImageByte croppedByte;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    public ImageByte getCroppedByte() {
        return croppedByte;
    }

    public void setCroppedByte(ImageByte croppedByte) {
        this.croppedByte = croppedByte;
    }
}