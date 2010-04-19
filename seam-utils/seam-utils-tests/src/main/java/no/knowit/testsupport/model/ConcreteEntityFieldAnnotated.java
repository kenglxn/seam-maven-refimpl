package no.knowit.testsupport.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class ConcreteEntityFieldAnnotated extends AbstractEntityFieldAnnotated implements Serializable {
  private static final long serialVersionUID = 3179935258851364014L;
  
  public enum Color {RED, YELLOW, GREEN};
  
  private String name;
  private Date dateOfBirth;
  private Color color;
  
  @Transient
  private long age;
}
