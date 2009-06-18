package no.knowit.m2m.model;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import no.knowit.entity.BaseEntity;

/**
 * @author Leif Olsen.
 * @version 1.0
 */
@Entity
public class Person extends BaseEntity<Long> {

  private static final long serialVersionUID = 1L;
  
  @Column(length=40, nullable=false)
  private String name;
  
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL) 
  @JoinTable(
      name = "PERSON_GROUP",
      joinColumns = @JoinColumn(name = "PERSON_ID"),
      inverseJoinColumns = @JoinColumn(name = "GROUP_ID")
  )
  private Set<Group> groups = new HashSet<Group>();
  
	public Person () {
  }

  public String getName() { 
  	return name; 
  }
  
  public void setName(String name) {
  	this.name = name;
  }

  public Set<Group> getGroups() {
  	return groups;
  }

	public void setGroups( Set<Group> groups ) {
  	this.groups = groups;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append("name='").append(name)
    	.append("'}");
    return sb.toString();    
  }  
}
