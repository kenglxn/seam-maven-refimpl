package net.glxn.site.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;
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
public class Page implements Serializable {
    private static final long serialVersionUID = -1L;

    private Long id;
    private Integer version;
    private String title;
    private String content;
    private Page parent;
    private List<Page> children = new ArrayList<Page>();
    private FileSet fileSet;
    private String fileSetPosition;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToOne
    @JoinColumn(name = "PARENT_ID", nullable=true)
    public Page getParent() {
        return parent;
    }

    public void setParent(Page parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy="parent")
    public List<Page> getChildren() {
        return children;
    }

    public void setChildren(List<Page> children) {
        this.children = children;
    }

    @Lob
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    @JoinColumn(name = "FILESET_ID", nullable=true)
    public FileSet getFileSet() {
        return fileSet;
    }

    public void setFileSet(FileSet fileSet) {
        this.fileSet = fileSet;
    }

    public String getFileSetPosition() {
        return fileSetPosition;
    }

    public void setFileSetPosition(String fileSetPosition) {
        this.fileSetPosition = fileSetPosition;
    }
}
