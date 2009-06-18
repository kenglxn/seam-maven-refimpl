package no.knowit.m2m.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.ManyToMany;

import no.knowit.entity.BaseEntity;

/*
 * @author Leif Olsen.
 * 
 * @version 1.0
 */
@Entity
public class Group extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Column(length = 10, nullable = false)
	private String name;

	@ManyToMany(mappedBy = "groups", cascade=CascadeType.ALL)
	private Set<Person> persons = new HashSet<Person>();
	
	public Group() {
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public Set<Person> getPersons(){
  	return persons;
  }

	public void setPersons( Set<Person> persons ) {
  	this.persons = persons;
  }

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder( super.toString() );
		sb.append( "name='" ).append( name ).append( "'}" );
		return sb.toString();
	}
}
