package org.open18.action;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.open18.auth.PasswordBean;
import org.open18.auth.PasswordManager;
import org.open18.model.Golfer;

@Name("registerAction")
public class RegisterAction {

	@Logger
	private Log log;

	private EntityManager entityManager;

	private FacesMessages facesMessages;

	private PasswordManager passwordManager;

	private Golfer newGolfer;

	private PasswordBean passwordBean;

	private String[] proStatusTypes = {};

	private List<String> specialtyTypes = new ArrayList<String>();

	public String[] getProStatusTypes() {
		return this.proStatusTypes;
	}

	public void setProStatusTypes(String[] types) {
		this.proStatusTypes = types;
	}

	public List<String> getSpecialtyTypes() {
		return specialtyTypes;
	}

	public void setSpecialtyTypes(List<String> specialtyTypes) {
		this.specialtyTypes = specialtyTypes;
	}
	
	public String register() {
		log.info("Registering golfer #{newGolfer.username}");
		if (!passwordBean.verify()) {
			facesMessages.addToControl("confirm", "value does not match password");
			return null;
		}

		newGolfer.setPasswordHash(passwordManager.hash(passwordBean.getPassword()));
		entityManager.persist(newGolfer);
		facesMessages.addFromResourceBundle("registration.welcome", newGolfer.getName());
		return "success";
	}
}
