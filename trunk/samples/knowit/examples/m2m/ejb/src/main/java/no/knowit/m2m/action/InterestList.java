package no.knowit.m2m.action;

import no.knowit.m2m.model.Interest;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("interestList")
public class InterestList extends EntityQuery<Interest> {
	
	@Override
  public String getEjbql()  { 
		return "select interest from Interest interest";
	}

	@Override
	public String getOrder() {
		if (super.getOrder() == null) {
			setOrder("name asc");
		}
		return super.getOrder();
	}
}
