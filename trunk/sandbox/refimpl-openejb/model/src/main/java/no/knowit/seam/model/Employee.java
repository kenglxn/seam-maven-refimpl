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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Embedded;
import javax.persistence.CascadeType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Employee implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Version
	private Long version;
	
	@Embedded
	private EmployeeDetail employeeDetail;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="department_id")
	private Department department;	
	
	@OneToOne(optional=false, cascade=CascadeType.ALL)
	@JoinColumn(name="parkingspace_id")
  private ParkingSpace parkingSpace;
	
	@ManyToMany(mappedBy="employees")
	private Collection<Project> projects;
	
	@OneToMany(mappedBy="asignee")
	private Collection<ProjectAssignment> projectAssignments;	
}
