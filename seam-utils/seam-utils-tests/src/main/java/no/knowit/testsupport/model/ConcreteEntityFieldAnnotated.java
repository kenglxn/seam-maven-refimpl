package no.knowit.testsupport.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class ConcreteEntityFieldAnnotated extends AbstractEntityFieldAnnotated implements Serializable {
  private static final long serialVersionUID = 3179935258851364014L;
  
  private String name;
  private Date dateOfBirth;
}
