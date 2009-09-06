package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.Product;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
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
}