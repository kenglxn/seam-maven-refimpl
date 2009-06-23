package no.knowit.m2m.model;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import no.knowit.entity.BaseEntity;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * @author Leif Olsen.
 * @version 1.0
 */
@Entity
public class Company  extends BaseEntity<Long> {
	
  @Length(max = 40) 
  @NotNull
  private String name;

	@ManyToMany(
    fetch = FetchType.LAZY, 
    cascade = { PERSIST, MERGE, REFRESH } 
  ) 
  @JoinTable(
		name = "Company_Contact",
		joinColumns = @JoinColumn( name = "company_id" ),
		inverseJoinColumns = @JoinColumn( name = "person_id" )
  )
	@OrderBy("name")
  private Set<Person> contactPersons = new LinkedHashSet<Person>(0);

	public Company () {
		super();
  }
	
	public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }

	public Set<Person> getContactPersons() {
    return contactPersons;
  }
	
	public void setContactPersons( Set<Person> contactPersons ) {
    this.contactPersons = contactPersons;
  }

	@Transient
	public String getContactPersonsAsString() {
		final StringBuilder sb = new StringBuilder();
		for ( Iterator<Person> i = contactPersons.iterator(); i.hasNext(); ) {
			sb.append( i.next().getName() );
			if ( i.hasNext() ) 
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
