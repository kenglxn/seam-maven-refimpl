package no.knowit.testsupport.model;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractEntityFieldAnnotated implements Serializable {
  private static final long serialVersionUID = -8153933318838927080L;

  @Id
  private Integer id;
    
  @Version
  private Integer version;
    
}
