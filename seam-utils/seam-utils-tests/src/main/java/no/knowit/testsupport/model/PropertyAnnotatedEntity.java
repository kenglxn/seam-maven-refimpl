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

  private static final int FOOBAR = 100;

  private Long id;

  private Long version;
  
  transient private Date cahcheTime;

  private Integer foo;
  
  private final int bar = 10;

  private String baz;
  
  public PropertyAnnotatedEntity() {
    super();
  }
  
  public PropertyAnnotatedEntity(int foo) {
    super();
    this.foo = foo;
  }

  public PropertyAnnotatedEntity(int foo, String baz) {
    super();
    this.foo = foo;
    this.baz = baz;
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

  /*
   * Avoid final properties in entities.
   * If you have a final field then the corresponding getter must be final
   */
  public final int getBar() {
    return bar;
  }
  
  /*
   * Avoid final properties in entities.
   * If you have a final field then the corresponding setter must be final
   */
  public final void setBar(int bar) {
    ;
  }

  public String getBaz() {
    return baz;
  }

  public void setBaz(String fooBar) {
    this.baz = fooBar;
  }
}
