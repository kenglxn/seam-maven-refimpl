package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import no.knowit.m2m.model.Person;

@Name("personHome")
public class PersonHome extends EntityHome<Person> {
	@RequestParameter
	Long personId;

	@Override
	public Object getId() {
		return personId == null ? super.getId() : personId;
	}

	@Override
	@Begin
	public void create() {
		super.create();
	}

}
