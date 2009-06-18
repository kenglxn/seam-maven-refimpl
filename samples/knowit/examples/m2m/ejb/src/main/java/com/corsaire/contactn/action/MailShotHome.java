package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import com.corsaire.contactn.model.MailShot;

@Name("mailShotHome")
public class MailShotHome extends EntityHome<MailShot>
{

    @RequestParameter 
    Long mailShotId;
    
    @Override
    public Object getId() 
    { 
        if (mailShotId==null)
        {
            return super.getId();
        }
        else
        {
            return mailShotId;
        }
    }
    
    @Override @Begin
    public void create() {
        super.create();
    }
 	
}
