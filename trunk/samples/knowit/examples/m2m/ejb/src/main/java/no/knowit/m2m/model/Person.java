package no.knowit.m2m.model;


import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import no.knowit.entity.BaseEntity;

import org.hibernate.validator.Email;
import org.hibernate.validator.NotNull;

/**
 * @author Leif Olsen.
 * @version 1.0
 */
@Entity
public class Person extends BaseEntity<Long> {

  private static final long serialVersionUID = 1L;
  
  @Column(length=40, nullable=false)
  private String name;

  @Email
	@NotNull
	private String email;
  
	@ManyToMany(
    fetch = FetchType.LAZY, 
    cascade = { PERSIST, MERGE, REFRESH } 
	) 
  @JoinTable(
		name = "Person_Interest",
		joinColumns = @JoinColumn( name = "person_id" ),
		inverseJoinColumns = @JoinColumn( name = "interest_id" )
  )
	@OrderBy("name")
  private List<Interest> interests = new ArrayList<Interest>();	

	@ManyToMany(
    fetch = FetchType.LAZY, 
    cascade = { PERSIST, MERGE, REFRESH } 
	) 
  @JoinTable(
		name = "Person_Language",
		joinColumns = @JoinColumn( name = "person_id" ),
		inverseJoinColumns = @JoinColumn( name = "language_id" )
  )
	@OrderBy("code")
  private List<Language> spokenLanguages = new ArrayList<Language>();	

	@ManyToMany(
    mappedBy = "contactPersons", 
    fetch = FetchType.LAZY, 
    cascade = { PERSIST, MERGE, REFRESH } 
	)
	@OrderBy("name")
	private List<Company> contactForCompanies = new ArrayList<Company>();	
	
	public Person () {
		super();
  }

  public String getName() { 
  	return name; 
  }
  
  public void setName(String name) {
  	this.name = name;
  }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
  public List<Interest> getInterests() {
  	return interests;
  }

	public void setInterests( List<Interest> interests ) {
  	this.interests = interests;
  }

	@Transient
	public String getInterestsAsString() {
		final StringBuilder sb = new StringBuilder();
		for ( Iterator<Interest> i = interests.iterator(); i.hasNext(); ) {
			sb.append( i.next().getName() );
			if ( i.hasNext() ) 
				sb.append( ", " );
		}
		return sb.toString();
	}

	public void setSpokenLanguages( List<Language> spokenLanguages ) {
	  this.spokenLanguages = spokenLanguages;
  }

	public List<Language> getSpokenLanguages() {
	  return spokenLanguages;
  }
	
	@Transient
	public String getSpokenLanguagesAsString() {
		final StringBuilder sb = new StringBuilder();
		for ( Iterator<Language> i = spokenLanguages.iterator(); i.hasNext(); ) {
			sb.append( i.next().getCode() );
			if (i.hasNext()) 
				sb.append( ", " );
		}
		return sb.toString();
	}

	public void setContactForCompanies( List<Company> contactForCompanies ) {
	  this.contactForCompanies = contactForCompanies;
  }

	public List<Company> getContactForCompanies() {
	  return contactForCompanies;
  }
	
	@Transient
	public String getContactForCompaniesAsString() {
		final StringBuilder sb = new StringBuilder();
		for ( Iterator<Company> i = contactForCompanies.iterator(); i.hasNext(); ) {
			sb.append( i.next().getName() );
			if (i.hasNext()) 
				sb.append( ", " );
		}
		return sb.toString();
	}

  @Override
  @Transient
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    return sb.append("name='").append(name).append("'}").toString();
  }
}
