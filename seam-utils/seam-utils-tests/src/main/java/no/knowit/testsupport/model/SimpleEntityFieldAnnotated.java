package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class SimpleEntityFieldAnnotated implements Serializable {
  private static final long serialVersionUID = 2818054409339379513L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
    
  @Version
  private Long version;
  
  private Integer foo;
  
  public SimpleEntityFieldAnnotated() {
    super();
  }
  
  public SimpleEntityFieldAnnotated(int foo) {
    super();
    this.foo = foo;
  }
  
//  public Long getId() {
//    return id;
//  }
}
