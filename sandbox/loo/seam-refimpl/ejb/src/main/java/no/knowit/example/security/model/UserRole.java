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

	private Long id;
  private Long version;
	private String name;
	private boolean conditional;
	private Set<UserRole> groups;

	public UserRole() {
	}
	
	@Id 
	@GeneratedValue
	@Column
	public Long getId() {
		return id;
	}

	// setter needed by JPA, but protected because it makes no sense for the application to assign an id.
	protected void setId(Long id) {
		this.id = id;
	}
	
  @Version
  @Column
  public Long getVersion() {
    return version;
  }
  
  protected void setVersion(Long version) {
		this.version = version;
	}
  
	@RoleName
	@NotNull
  @Column(unique=true, nullable=false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@RoleConditional
	public boolean isConditional() {
		return conditional;
	}
	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}
	
	@RoleGroups
	@ManyToMany
	@JoinTable(name = "UserRoleGroup", 
			joinColumns = @JoinColumn(name = "roleId"), 
			inverseJoinColumns = @JoinColumn(name = "memberOfRoleId")
	)
	public Set<UserRole> getGroups() {
		return groups;
	}
	
	public void setGroups(Set<UserRole> groups) {
		this.groups = groups;
	}

}
