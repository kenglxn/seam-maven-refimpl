package no.knowit.m2m.action;

import no.knowit.m2m.model.Language;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

@Name("languageList")
public class LanguageList extends EntityQuery<Language> {

	@Logger
	private Log log;
	
	@Override
  public String getEjbql()  { 
		log.debug("**** getEjbql ****");
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
