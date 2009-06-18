package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("profileList")
public class ProfileList extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "select profile from Profile profile";
    }
}
