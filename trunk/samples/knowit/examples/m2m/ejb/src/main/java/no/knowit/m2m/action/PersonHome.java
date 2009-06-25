package no.knowit.m2m.action;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import no.knowit.m2m.model.Company;
import no.knowit.m2m.model.Person;

@Name("personHome")
public class PersonHome extends EntityHome<Person> {
	
	@Logger
	private Log log;

	@RequestParameter
	private Long personId;

	private List<Company> contactForCompanies;
	
	@Override
	public Object getId() {
		return personId == null ? super.getId() : personId;
	}

	@Override
	@Begin
	public void create() {
		super.create();
	}

	/*
	@Factory(value = "person")
	public Person initPerson() {
		log.info("Getting person");
		return getInstance();
	}
	*/

	private void createOrUpdate() {
		
		// Update the owning side (Company)
		// The owning association is maintained by a Hash collection
		// which helps us avoid redundant entity instances.

		// 1: Remove original
		for( Company c : getInstance().getContactForCompanies() ) {
			c.getContactPersons().remove( getInstance() );
		}
		
		// 2: Update inverse side (Person)
		getInstance().setContactForCompanies( contactForCompanies );
		
		// 3: Add changes  to owning side
		for( Company c : contactForCompanies ) {
			c.getContactPersons().add( getInstance() );
		}
		
		// Note: This is not among the most efficient algorithms on the planet -
		// better strategies can be found :)
	}
	
	@Override
	public String update() {
		createOrUpdate();
		return super.update();
	}

	@Override
	public String persist() {
		createOrUpdate();
		return super.persist();
	}
  
	public List<Company> getContactForCompanies() {
		contactForCompanies =  getInstance() == null 
			? null : new ArrayList<Company>(getInstance().getContactForCompanies());
	
		return contactForCompanies;
  }
	
	public void setContactForCompanies( List<Company> contactForCompanies ) {
		this.contactForCompanies = contactForCompanies;
  }

}
