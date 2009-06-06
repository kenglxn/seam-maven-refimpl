package org.open18.action;

import org.open18.model.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("facilityHome")
public class FacilityHome extends EntityHome<Facility> {

	public void setFacilityId(Long id) {
		setId(id);
	}

	public Long getFacilityId() {
		return (Long) getId();
	}

	@Override
	protected Facility createInstance() {
		Facility facility = new Facility();
		return facility;
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public Facility getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Course> getCourses() {
		return getInstance() == null ? null : new ArrayList<Course>(
				getInstance().getCourses());
	}

}
