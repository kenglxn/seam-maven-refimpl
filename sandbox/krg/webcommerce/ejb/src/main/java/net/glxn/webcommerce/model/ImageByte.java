package net.glxn.webcommerce.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Version;
import javax.persistence.Lob;
import javax.persistence.Basic;
import java.io.Serializable;

@Entity
public class ImageByte implements Serializable {
    private Long id;
    private Integer version;
    private static final long serialVersionUID = -7774097431409781164L;
    private byte[] image;

    public ImageByte() {
    }

    public ImageByte(byte[] image) {
        this.image = image;
    }

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
}
