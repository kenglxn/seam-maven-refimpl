package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Category;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("categoryList")
public class CategoryList extends EntityQuery<Category> {

    public CategoryList() {
        setEjbql("select cat from Category cat left join fetch cat.children");
    }

}