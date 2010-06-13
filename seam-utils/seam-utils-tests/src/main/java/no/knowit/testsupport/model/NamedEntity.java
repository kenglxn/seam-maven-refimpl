package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name="ANamedEntity")
public class NamedEntity implements Serializable {
  private static final long serialVersionUID = -9108257112671631060L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer identity;
    
  @Version
  private Integer version;
  
  private Integer foo;
  
  public NamedEntity() {
  }
  
  public NamedEntity(final Integer foo) {
    this.setFoo(foo);
  }

  public void setFoo(Integer foo) {
    this.foo = foo;
  }

  public Integer getFoo() {
    return foo;
  }
}
