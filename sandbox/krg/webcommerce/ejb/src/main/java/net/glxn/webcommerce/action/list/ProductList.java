package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.framework.EntityQuery;

import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("productList")
public class ProductList extends EntityQuery<Product> {
    private static final long serialVersionUID = -3444306007404323006L;

    @In(required = false)
    @Out(required = false)
    Long catId;

    @In(required = false)
    @Out(required = false)
    String searchString;

    @In(required = false)
    @Out(required = false)
    List<Product> searchResults;

    @DataModel
    List<Product> productForCategory;

    public ProductList() {
        setEjbql("select distinct(p) from Product p left join fetch p.files");
    }

    @Factory("productForCategory")
    public List<Product> getProductForCategory() {
        if (catId == null) {
            return getResultList();
        }
        setEjbql("select distinct(p) from Product p left join fetch p.files where p.category.id = #{catId} ");
        return getResultList();
    }

    public String search() {
        setEjbql("select distinct(p) from Product p " +
                "   left join fetch p.files" +
                "       where lower(p.name) like concat('%', concat(lower(#{searchString}), '%')) " +
                "           or lower(p.description) like concat('%', concat(lower(#{searchString}), '%'))");
        searchResults = getResultList();
        return "search";
    }
}