package no.knowit.testsupport.model.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PartTimeEmployee extends CompanyEmployee {
  @Column(name="H_RATE")
  private float hourlyRate;
}
