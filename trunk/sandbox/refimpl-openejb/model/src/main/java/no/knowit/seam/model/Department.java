package no.knowit.seam.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Department implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Version
	private Long version;
	
  @Column(unique=true)
	@NotNull
	@Length(max = 100)
	private String name;
	
	@OneToMany(mappedBy="department")
	private Collection<Employee> employees;
}
