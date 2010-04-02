package no.knowit.testsupport.model.inheritance;

import javax.persistence.Entity;

@Entity
public class FullTimeEmployee extends CompanyEmployee {
  private long salary;
  private long pension;
}
