package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import com.corsaire.contactn.model.Content;

@Name("contentHome")
public class ContentHome extends EntityHome<Content>
{

    @RequestParameter 
    Long contentId;
    
    @Override
    public Object getId() 
    { 
        if (contentId==null)
        {
            return super.getId();
        }
        else
        {
            return contentId;
        }
    }
    
    @Override @Begin
    public void create() {
        super.create();
    }
 	
}
