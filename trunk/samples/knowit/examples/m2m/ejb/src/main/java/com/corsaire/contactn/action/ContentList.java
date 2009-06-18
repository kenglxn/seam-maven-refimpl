package com.corsaire.contactn.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("contentList")
public class ContentList extends EntityQuery
{
    @Override
    public String getEjbql() 
    { 
        return "select content from Content content";
    }
}
