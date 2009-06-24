package no.knowit.m2m.action;

import no.knowit.m2m.model.Interest;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("languageList")
public class LanguageList extends EntityQuery<Interest> {
	
	@Override
  public String getEjbql()  { 
		return "select language from Language language";
	}
	
	@Override
	public String getOrder() {
		if (super.getOrder() == null) {
			setOrder("code asc");
		}
		return super.getOrder();
	}

}
