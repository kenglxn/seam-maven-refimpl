package no.knowit.testsupport.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ConcreteEntityPropertyAnnotated extends AbstractEntityPropertyAnnotated implements Serializable {
  private static final long serialVersionUID = -6191438010226617582L;
  
  private String name;
  private Date dateOfBirth;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Date getDateOfBirth() {
    return dateOfBirth;
  }
  
  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }
}
