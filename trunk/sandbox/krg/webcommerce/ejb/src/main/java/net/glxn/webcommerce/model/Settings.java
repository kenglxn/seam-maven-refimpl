package net.glxn.webcommerce.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 04.aug.2009
 * Time: 20:24:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Settings implements Serializable {
    private Long id;
    private String filePath;
    private static final long serialVersionUID = 1162616691505820101L;

    @Id @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
