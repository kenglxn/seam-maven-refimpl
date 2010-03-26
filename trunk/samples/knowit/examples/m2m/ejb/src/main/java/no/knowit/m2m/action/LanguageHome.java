package no.knowit.m2m.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import no.knowit.m2m.model.Interest;

@Name("languageHome")
public class LanguageHome extends EntityHome<Interest> {
	@RequestParameter
	Long languageId;

	@Override
	public Object getId() {
		return languageId == null ? super.getId() : languageId;
	}

	@Override
	@Begin
	public void create() {
		super.create();
	}

}
