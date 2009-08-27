package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Category;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("categoryList")
public class CategoryList extends EntityQuery<Category> {
    private static final long serialVersionUID = 1567750404417961477L;

    public CategoryList() {
        setEjbql("select cat from Category cat left join fetch cat.children");
    }

}