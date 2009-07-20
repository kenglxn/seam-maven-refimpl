package net.glxn.site.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 06.jul.2009
 * Time: 17:28:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class FileSet implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id;
    private Integer version;
    private String name;
    private String description;
    private List<File> files = new ArrayList<File>();
    private List<Page> hostingPages = new ArrayList<Page>();


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

    @OneToMany(mappedBy="fileSet")
    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @OneToMany(mappedBy="fileSet")
    public List<Page> getHostingPages() {
        return hostingPages;
    }

    public void setHostingPages(List<Page> hostingPages) {
        this.hostingPages = hostingPages;
    }
}