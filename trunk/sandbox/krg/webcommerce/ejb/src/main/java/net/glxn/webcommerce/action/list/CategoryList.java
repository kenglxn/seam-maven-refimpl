package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Category;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Log;

import java.util.List;

@Name("categoryList")
public class CategoryList extends EntityQuery<Category> {
    private static final long serialVersionUID = 1567750404417961477L;

    @Logger
    private Log log;

    @Out(required = false, scope = ScopeType.CONVERSATION)
    List<Category> parentCategories;

    public CategoryList() {
        setEjbql("select distinct(c) from Category c left join fetch c.children ");
    }

    @Factory("parentCategories")
    public void initParentCategories() {
        log.info("running initParentCategories");
        setEjbql("select distinct(c) from Category c left join fetch c.children where c.parent is null");
        parentCategories = getResultList();
    }

}