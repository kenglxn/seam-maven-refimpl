package no.knowit.example.security.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.jboss.seam.annotations.security.permission.PermissionAction;
import org.jboss.seam.annotations.security.permission.PermissionDiscriminator;
import org.jboss.seam.annotations.security.permission.PermissionRole;
import org.jboss.seam.annotations.security.permission.PermissionTarget;
import org.jboss.seam.annotations.security.permission.PermissionUser;

@Entity
public class UserPermission implements Serializable {
	private static final long serialVersionUID = -5628863031792429938L;

	private Long id;
	private Long version;
	private String recipient;
	private String target;
	private String action;
	private String discriminator;
	
	public UserPermission() {
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
  
	
	@PermissionUser
	@PermissionRole
  @Column
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@PermissionTarget
  @Column
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}

	@PermissionAction
  @Column
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	@PermissionDiscriminator
  @Column
	public String getDiscriminator() {
		return discriminator;
	}
	
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

}
