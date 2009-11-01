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

	@Id 
	@GeneratedValue
	private Long id;
	
  @Version 
	private Long version;
  
	@NotNull
	@UserPrincipal
  @Column(unique=true, nullable=false)
	private String username;
	
	@UserPassword(hash = "SHA")
	private String passwordHash;
	
  //@PasswordSalt
	//private String passwordSalt;
	
	@UserEnabled
	private boolean enabled;
	
	@UserRoles
  @ManyToMany
	@JoinTable(name = "UserAccountRole", 
			joinColumns = @JoinColumn(name = "userAccountId"), 
			inverseJoinColumns = @JoinColumn(name = "userRoleId")
	)
	private Set<UserRole> roles;


  public Long getId() {
    return id;
  }
  
  public Long getVersion() {
    return version;
  }

	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/*
	public String getPasswordSalt() {
		return passwordSalt;
	}
	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}
  */
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<UserRole> getRoles() {
		return roles;
	}
	public void setRoles(Set<UserRole> roles) {
		this.roles = roles;
	}
}
