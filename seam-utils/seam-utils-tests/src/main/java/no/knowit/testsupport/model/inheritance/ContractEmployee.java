package no.knowit.testsupport.model.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ContractEmployee extends AbstractContractEmployee {
  @Column(name="D_RATE")
  private int dailyRate; 
  private int term;
}
