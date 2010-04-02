package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class AbstractEntityFieldAnnotated implements Serializable {
  private static final long serialVersionUID = -8153933318838927080L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer identity;
    
  @Version
  private Integer version;
    
}
