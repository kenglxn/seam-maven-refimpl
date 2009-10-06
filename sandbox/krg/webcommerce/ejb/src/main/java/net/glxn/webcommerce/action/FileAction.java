package net.glxn.webcommerce.action;

import net.glxn.webcommerce.action.home.FileHome;
import net.glxn.webcommerce.action.home.ProductHome;
import net.glxn.webcommerce.action.home.PageHome;
import net.glxn.webcommerce.model.File;
import net.glxn.webcommerce.model.Product;
import net.glxn.webcommerce.model.Page;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.io.Serializable;

@Scope(ScopeType.CONVERSATION)
@Name("fileAction")
public class FileAction implements Serializable {

    @In(required = false)
    ProductHome productHome;

    @In(required = false)
    PageHome pageHome;

    @In
    FileHome fileHome;

    private static final long serialVersionUID = 7041706527330878277L;

    public void removeFileFromProductAndDeleteFile() {
        Product product = productHome.getInstance();
        File file = fileHome.getInstance();
        product.getFiles().remove(file);
        fileHome.remove();
    }

    public void removeFileFromPageAndDeleteFile() {
        Page page = pageHome.getInstance();
        File file = fileHome.getInstance();
        page.getFiles().remove(file);
        fileHome.remove();
    }
}
