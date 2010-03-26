package no.knowit.m2m.action;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityQuery;
import no.knowit.m2m.model.Person;

@Name("personList")
public class PersonList extends EntityQuery<Person> {
	
	@Override
  public String getEjbql()  { 
		return "select person from Person person";
  }
  
	@Override
	public String getOrder() {
		if (super.getOrder() == null) {
			setOrder("name asc");
		}
		return super.getOrder();
	}

	public void setResultList(List<Person> p) {
		// Needed to shut up RichFaces ListShuttle in company.xhtml
		;
	}
}
