package com.corsaire.contactn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Recipient implements Serializable {
	
	//seam-gen attributes (you should probably edit these)
	private Long id;
	private String name;
	private String email;
	private List<Profile> profiles = new ArrayList<Profile>();
	
    //add additional entity attributes
	
	//seam-gen attribute getters/setters with annotations (you probably should edit)
		
	@Id @GeneratedValue
	public Long getId() {
	     return id;
	}

	public void setId(Long id) {
	     this.id = id;
	}
	
	@NotNull
	@Length(max=30)
	public String getName() {
	     return name;
	}

	public void setName(String name) {
	     this.name = name;
	}   	

	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL) 
    @JoinTable(
        name = "RECIPIENT_PROFILE",
        joinColumns = @JoinColumn(name = "RECIPIENT_ID"),
        inverseJoinColumns = @JoinColumn(name = "PROFILE_ID")
    )
    @org.hibernate.annotations.ForeignKey(name = "RECIPIENT_PROFILE_RECIPIENT_ID", inverseName = "RECIPIENT_PROFILE_PROFILE_ID")
	public List<Profile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}
	
	public void addProfile(Profile p) {
		this.getProfiles().add(p);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Recipient other = (Recipient) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Transient
	public String getProfilesString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Profile> i = profiles.iterator();i.hasNext();) {
			sb.append(i.next().getName());
			if (i.hasNext()) sb.append(",");
		}
		return sb.toString();
	}

	@NotNull
	@Email
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
