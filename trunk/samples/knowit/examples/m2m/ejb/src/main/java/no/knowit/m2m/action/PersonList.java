package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import no.knowit.m2m.model.Person;

@Name("personList")
public class PersonList extends EntityQuery<Person> {
	
	public PersonList() {
		setEjbql( "select person from Person person" );
	}
}
