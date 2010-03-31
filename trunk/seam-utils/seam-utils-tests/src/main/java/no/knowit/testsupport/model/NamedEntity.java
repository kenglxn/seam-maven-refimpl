package no.knowit.testsupport.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name="aNamedEntity")
public class NamedEntity implements Serializable {
  private static final long serialVersionUID = -9108257112671631060L;

  @Id
  private Integer identity;
    
  @Version
  private Integer version;
  
  private Integer foo;
}
