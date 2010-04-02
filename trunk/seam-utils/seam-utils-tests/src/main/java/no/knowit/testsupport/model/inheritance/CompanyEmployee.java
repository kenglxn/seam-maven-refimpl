package no.knowit.testsupport.model.inheritance;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class CompanyEmployee extends AbstractEmployee {
  private int vacation;
}
