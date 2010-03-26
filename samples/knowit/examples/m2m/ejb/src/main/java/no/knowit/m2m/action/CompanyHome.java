package no.knowit.m2m.action;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import no.knowit.m2m.model.Company;
import no.knowit.m2m.model.Person;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

@Name("companyHome")
public class CompanyHome extends EntityHome<Company>
{
    @RequestParameter 
    private Long companyId;
    
  	@Logger
  	private Log log;

  	private List<Person> contactPersons;
    
    /*
  	@Factory("company")
  	public Company initCompany() {
  		log.info("Getting company");
  		return getInstance();
  	}
  	*/
    
    @Override
    public Object getId() {
      return companyId == null ? super.getId() : companyId;
    }

    @Override 
    @Begin
    public void create() {
      super.create();
    }

  	@Override
  	public String update() {
  		getInstance().setContactPersons( new LinkedHashSet<Person>(contactPersons) );
  		return super.update();
  	}

  	@Override
  	public String persist() {
  		getInstance().setContactPersons( new LinkedHashSet<Person>(contactPersons) );
  		return super.persist();
  	}
    
    public List<Person> getContactPersons() {
  		contactPersons =  getInstance() == null 
  			? null : new ArrayList<Person>(getInstance().getContactPersons() );
  		
  		return contactPersons;
    }

    public void setContactPersons( List<Person> contactPersons ) {
	    this.contactPersons = contactPersons;
    }
}
