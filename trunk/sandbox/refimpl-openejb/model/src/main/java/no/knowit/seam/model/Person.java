package no.knowit.seam.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Person extends MyTransientClass {
  @Column
	private String navn;
}
