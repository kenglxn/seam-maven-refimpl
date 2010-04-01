package no.knowit.testsupport.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class SimpleEntityPropertyAnnotated implements Serializable {
  private static final long serialVersionUID = 2818054409339379513L;

  private Long id;
  private Long version;
  private Integer bar;
  
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

  public Integer getBar() {
    return bar;
  }

  public void setBar(Integer bar) {
    this.bar = bar;
  }

}
