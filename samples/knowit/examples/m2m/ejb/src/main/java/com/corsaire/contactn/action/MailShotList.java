package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("mailShotList")
public class MailShotList extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "select mailShot from MailShot mailShot";
    }
}
