package no.knowit.m2m.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import no.knowit.m2m.model.Company;
import no.knowit.m2m.model.Person;

@Name("personHome")
public class PersonHome extends EntityHome<Person> {
	
	@RequestParameter
	private Long personId;

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

	public List<Company> getContactForCompanies() {
		return getInstance() == null 
			? null : new ArrayList<Company>(getInstance().getContactForCompanies());
  }
}
