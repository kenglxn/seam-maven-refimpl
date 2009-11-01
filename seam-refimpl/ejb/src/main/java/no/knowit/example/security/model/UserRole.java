package no.knowit.example.security.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.security.management.RoleConditional;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

@Entity
public class UserRole implements Serializable {
	private static final long serialVersionUID = 9177366120789064801L;

	@Id
	@GeneratedValue
	private Long id;

	@Version 
  private Long version;
	
	@RoleName
	@NotNull
  @Column(unique=true, nullable=false)
	private String name;
	
	@RoleConditional
	private boolean conditional;
	
	@RoleGroups
	@ManyToMany
	@JoinTable(name = "UserRoleGroup", 
			joinColumns = @JoinColumn(name = "roleId"), 
			inverseJoinColumns = @JoinColumn(name = "memberOfRoleId")
	)
	private Set<UserRole> groups;

	public Long getId() {
		return id;
	}

  public Long getVersion() {
    return version;
  }
  
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isConditional() {
		return conditional;
	}
	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}
	
	public Set<UserRole> getGroups() {
		return groups;
	}
	public void setGroups(Set<UserRole> groups) {
		this.groups = groups;
	}

}
