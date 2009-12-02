package no.knowit.seam.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Embeddable
public class EmployeeDetail {

  @Column
	@NotNull
	@Length(max = 100)
	private String firstName;
	
  @Column
	@NotNull
	@Length(max = 100)
	private String lastName;
}
