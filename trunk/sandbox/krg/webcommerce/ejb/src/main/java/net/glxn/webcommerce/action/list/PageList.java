package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.action.home.PageHome;
import net.glxn.webcommerce.model.Page;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;
import java.util.List;

@Name("pageList")
public class PageList extends EntityQuery<Page> {

    private static final long serialVersionUID = 4275971785526395702L;

    @In
    EntityManager entityManager;

    @In(required = false)
    PageHome pageHome;

    @In(required = false)
    @Out(required = false)
    Page child;
    @In(required = false)
    @Out(required = false)
    Long childId;

    @Out(required = false, scope = ScopeType.CONVERSATION)
    private List<Page> parentPages;

    @Out(required = false, scope = ScopeType.PAGE)
    private List<Page> childPages;

    public PageList() {
        setEjbql("select distinct(page) from Page page left join fetch page.children ");
    }

    @Factory("parentPages")
    public void initParentPages() {
        setEjbql("select distinct(page) from Page page left join fetch page.children where page.parent is null");
        parentPages = getResultList();
    }

    @Factory("childPages")
    public void initChildPages() {
        setEjbql("select distinct(page) from Page page left join fetch page.children where page.parent.id = #{pageHome.instance.id}");
        childPages = getResultList();
    }

    public Page childById() {
        if (child == null || !childId.equals(child.getId())) {
            setEjbql("select distinct(page) from Page page left join fetch page.children where page.id = #{childId}");
            child = getSingleResult();
        }
        return child;
    }
}