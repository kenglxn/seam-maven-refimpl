package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.framework.EntityQuery;

import java.util.List;

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