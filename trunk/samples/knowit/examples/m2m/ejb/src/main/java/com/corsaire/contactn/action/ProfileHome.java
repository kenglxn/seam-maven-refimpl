package com.corsaire.contactn.action;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import com.corsaire.contactn.model.Profile;

@Name("profileHome")
public class ProfileHome extends EntityHome<Profile>
{

    @RequestParameter 
    Long profileId;
    
    @Factory("profile")
    public Profile initProfile() { return getInstance(); };
    
    @Override
    public Object getId() 
    { 
        if (profileId==null)
        {
            return super.getId();
        }
        else
        {
            return profileId;
        }
    }
    
    @Override @Begin
    public void create() {
        super.create();
    }
 	
}
