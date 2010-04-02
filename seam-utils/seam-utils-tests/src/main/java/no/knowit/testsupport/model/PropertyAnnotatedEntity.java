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
public class PropertyAnnotatedEntity implements Serializable {
  private static final long serialVersionUID = 2818054409339379513L;

  private Long id;

  private Long version;
  
  private Date cahcheTime;

  private Integer foo;
  
  private final int bar = 10;
  
  private static final int BAZ = 100;

  public PropertyAnnotatedEntity() {
    super();
  }
  
  public PropertyAnnotatedEntity(int foo) {
    super();
    this.foo = foo;
  }

  @Id
  @GeneratedValue(strategy = IDENTITY)
  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  @Version
  public Long getVersion() {
    return version;
  }

  protected void setVersion(Long version) {
    this.version = version;
  }

  @Transient
  public Date getCahcheTime() {
    return cahcheTime;
  }

  public void setCahcheTime(Date cahcheTime) {
    this.cahcheTime = cahcheTime;
  }

  public Integer getFoo() {
    return foo;
  }

  public void setFoo(Integer foo) {
    this.foo = foo;
  }

  public int getBar() {
    return bar;
  }
  
  public void setBar(int bar) {
    ;
  }
}
