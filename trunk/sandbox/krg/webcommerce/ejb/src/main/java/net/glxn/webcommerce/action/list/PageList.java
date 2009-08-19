

import net.glxn.webcommerce.model.Page;
import net.glxn.webcommerce.action.home.PageHome;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;

import javax.persistence.EntityManager;
import java.util.List;

@Name("pageList")
public class PageList extends EntityQuery<Page> {

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

    public PageList() {
        setEjbql("select page from Page page");
    }

    public List<Page> getParentPages() {
        return entityManager.createQuery("select page from Page page where page.parent is null")
                .getResultList();
    }

    public List<Page> getChildPages() {
        return entityManager.createQuery("select page from Page page where page.parent.id = :pageId")
                .setParameter("pageId", pageHome.getId())
                .getResultList();
    }

    public Page childById() {
        if(child == null || !childId.equals(child.getId())) {
            child = (Page) entityManager.createQuery("select page from Page page where page.id = :pageId")
                .setParameter("pageId", childId)
                .getSingleResult();
        }
        return child;
    }
}