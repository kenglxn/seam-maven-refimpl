package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import no.knowit.m2m.model.Interest;

@Name("interestHome")
public class InterestHome extends EntityHome<Interest> {
	@RequestParameter
	Long interestId;

	@Override
	public Object getId() {
		return interestId == null ? super.getId() : interestId;
	}

	@Override
	@Begin
	public void create() {
		super.create();
	}

}
