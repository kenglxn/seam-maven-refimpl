package no.knowit.m2m.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

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
		name = "PERSON_INTEREST",
		joinColumns = @JoinColumn(name = "PERSON_ID"),
		inverseJoinColumns = @JoinColumn(name = "INTEREST_ID")
  )
	private List<Interest> interests = new ArrayList<Interest>();
  
	public Person () {
  }

  public String getName() { 
  	return name; 
  }
  
  public void setName(String name) {
  	this.name = name;
  }

  public List<Interest> getInterests() {
  	return interests;
  }

	public void setInterests( List<Interest> interests ) {
  	this.interests = interests;
  }

	@Transient
	public String getInterestsString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Interest> i = interests.iterator(); i.hasNext();) {
			sb.append(i.next().getName());
			if (i.hasNext()) sb.append(",");
		}
		return sb.toString();
	}

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(super.toString());
    sb.append("name='").append(name)
    	.append("'}");
    return sb.toString();    
  }  
}
