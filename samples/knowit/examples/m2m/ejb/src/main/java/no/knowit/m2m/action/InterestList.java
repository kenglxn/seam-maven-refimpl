package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import no.knowit.m2m.model.Interest;

@Name("interestList")
public class InterestList extends EntityQuery<Interest> {
	
	public InterestList() {
		setEjbql( "select interest from Interest interest" );
	}
}
