package no.knowit.seam.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Table;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "ProjectAssignment")
@org.hibernate.annotations.Table(
  appliesTo = "ProjectAssignment", indexes =
    @org.hibernate.annotations.Index(
      name = "idx_projectassignments", 
        columnNames = { "project_id", "employee_id" }
    )
)
public class ProjectAssignment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Version
	private Long version;
	
  @Temporal(TemporalType.DATE)
	private Date embarkedDate;
	
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	@ManyToOne
	@JoinColumn(name="employee_id")
	private Employee asignee;
}
