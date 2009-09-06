package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

@Scope(ScopeType.CONVERSATION)
@Name("productSearch")
public class ProductSearch extends EntityQuery<Product> {

    private static final long serialVersionUID = -2229449712146419611L;

    @In
    @Out
    String searchString;

    public ProductSearch() {
        setEjbql("select distinct(p) from Product p " +
                "   left join fetch p.files" +
                "       where lower(p.name) like concat('%', concat(lower(#{searchString}), '%')) " +
                "           or lower(p.description) like concat('%', concat(lower(#{searchString}), '%'))");
    }

    public String search() {
        return "search";
    }
}