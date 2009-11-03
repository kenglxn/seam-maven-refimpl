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
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

@Entity
public class UserAccount implements Serializable {
	private static final long serialVersionUID = 6368734442192368866L;

	private Long id;
	private Long version;
	private String username;
	private String passwordHash;
	//private String passwordSalt;
	private boolean enabled;
	private Set<UserRole> roles; 

	public UserAccount() {
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

	@NotNull
	@UserPrincipal
  @Column(unique = true, nullable = false)
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	@UserPassword(hash = "SHA")
  @Column
	public String getPasswordHash() {
		return passwordHash;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/*
  @PasswordSalt
  @Column
	public String getPasswordSalt() {
		return passwordSalt;
	}
	
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
  */
	
	@UserEnabled
  @Column
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@UserRoles
  @ManyToMany
	@JoinTable(name = "UserAccountRole", 
			joinColumns = @JoinColumn(name = "userAccountId"), 
			inverseJoinColumns = @JoinColumn(name = "userRoleId")
	)
	public Set<UserRole> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}
}
