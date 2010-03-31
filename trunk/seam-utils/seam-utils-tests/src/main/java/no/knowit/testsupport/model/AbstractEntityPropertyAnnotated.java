package no.knowit.testsupport.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class AbstractEntityPropertyAnnotated implements Serializable {

  private static final long serialVersionUID = -7926430827533830634L;

  private Integer id;
  private Integer version;

  @Id
  public Integer getIdentity() {
    return id;
  }

  protected void setIdentity(Integer id) {
    this.id = id;
  }

  @Version
  public Integer getVersion() {
    return version;
  }

  protected void setVersion(Integer version) {
    this.version = version;
  }

}
