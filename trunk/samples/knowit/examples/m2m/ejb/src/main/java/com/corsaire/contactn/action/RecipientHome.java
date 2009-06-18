package com.corsaire.contactn.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import com.corsaire.contactn.model.Profile;
import com.corsaire.contactn.model.Recipient;

@Name("recipientHome")
public class RecipientHome extends EntityHome<Recipient> {

	@RequestParameter
	Long recipientId;

	@Logger
	Log log;

	@Factory("recipient")
	public Recipient initRecipient() {
		log.info("Getting recipient");
		return getInstance();
	}

	public String removeFromProfile(Profile p) {
		if (getInstance().getProfiles().contains(p)) {
			getInstance().getProfiles().remove(p);
			this.update();
			return null;
		}
		return "failure";
		
	}
	
	@Override
	public Object getId() {
		if (recipientId == null) {
			return super.getId();
		} else {
			return recipientId;
		}
	}

	@Override
	@Begin(join=true)
	public void create() {
		super.create();
	}

}
