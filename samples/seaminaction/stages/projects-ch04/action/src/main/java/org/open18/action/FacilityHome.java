package org.open18.action;

import org.open18.model.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.EntityNotFoundException;

@Name("facilityHome")
public class FacilityHome extends EntityHome<Facility> {

	private boolean enterCourse = true;
	
	private String lastStateChange;
	
	public String getLastStateChange() {
		return this.lastStateChange;
	}
	
	public void setEnterCourse(boolean enterCourse) {
		this.enterCourse = enterCourse;
	}	
	
	public boolean isEnterCourse() {
		return enterCourse;
	}

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
	
	public String validateEntityFound() {
		try {
			this.getInstance();
		} catch (EntityNotFoundException e) {
			return "invalid";
		}

		return this.isManaged() ? "valid" : "invalid";
	}
	
	@Override
	public String persist() {
		lastStateChange = super.persist();
		return lastStateChange;
	}

	@Override
	public String remove() {
		lastStateChange = super.remove();
		return lastStateChange;
		}

	@Override
	public String update() {
		lastStateChange = super.update();
		return lastStateChange;
	}

}
