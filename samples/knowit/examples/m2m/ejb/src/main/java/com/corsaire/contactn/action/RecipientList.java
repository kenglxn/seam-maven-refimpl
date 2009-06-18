package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("recipientList")
public class RecipientList extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
      return "select recipient from Recipient recipient";
    }
}
