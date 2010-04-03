package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
public class FieldAnnotatedEntity implements Serializable {
  private static final long serialVersionUID = 2818054409339379513L;

  private static final int FOOBAR = 100;
  
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
    
  @Version
  private Long version;
  
  @Transient
  private Date cahcheTime;
  
  private Integer foo;
  
  private final int bar = 10;
  
  private String baz;
  
  public FieldAnnotatedEntity() {
    super();
  }
  
  public FieldAnnotatedEntity(int foo) {
    super();
    this.foo = foo;
  }

  public FieldAnnotatedEntity(int foo, String baz) {
    super();
    this.foo = foo;
    this.baz = baz;
  }
}
