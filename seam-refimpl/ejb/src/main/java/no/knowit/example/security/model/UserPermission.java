package no.knowit.example.security.model;

import java.io.Serializable;

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

	@Id
	@GeneratedValue
	private Long id;
	
  @Version 
	private Long version;
	
	@PermissionUser
	@PermissionRole
	private String recipient;
	
	@PermissionTarget
	private String target;
	
	@PermissionAction
	private String action;
	
	@PermissionDiscriminator
	private String discriminator;

	public Long getId() {
		return id;
	}

  public Long getVersion() {
    return this.version;
  }
  
	public String getRecipient() {
		return recipient;
	}
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getDiscriminator() {
		return discriminator;
	}
	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}

}
