package net.glxn.webcommerce.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table
public class Settings implements Serializable {
    private Long id;
    private String filePathServer;
    private String filePathClient;
    private static final long serialVersionUID = 1162616691505820101L;
    private Integer version;

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

    public String getFilePathServer() {
        return filePathServer;
    }

    public void setFilePathServer(String filePathServer) {
        this.filePathServer = filePathServer;
    }

    public String getFilePathClient() {
        return filePathClient;
    }

    public void setFilePathClient(String filePathClient) {
        this.filePathClient = filePathClient;
    }

    private Category defaultCategory;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Category getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(Category defaultCategory) {
        this.defaultCategory = defaultCategory;
    }
}
