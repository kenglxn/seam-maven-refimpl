package net.glxn.webcommerce.util;

import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import net.glxn.webcommerce.model.*;
import net.glxn.webcommerce.action.home.*;
import net.glxn.webcommerce.action.upload.FileUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ken
 * Date: 26.aug.2009
 * Time: 18:49:38
 * To change this template use File | Settings | File Templates.
 */
@Install(debug = true)
@Name("dataPopulator")
public class DataPopulator {

    @In(create = true)
    UserHome userHome;

    @In(create = true)
    PageHome pageHome;

    @In(create = true)
    ProductHome productHome;

    @In(create = true)
    CategoryHome categoryHome;

    @In(create = true)
    FileHome fileHome;

    private final String imgBase = "C:\\Users\\ken\\Pictures\\";

    @Observer("org.jboss.seam.postInitialization")
    public void observe() {
        createUsers();
        createPages();
        Category category = categoryHome.getInstance();
        category.setName("testcat");
        categoryHome.persist();

        java.io.File image = new java.io.File(imgBase+"coolgif.gif");
        byte[] byteFromFile = new byte[0];
        try {
            byteFromFile = FileUtil.getByteFromFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(byteFromFile.length > 0) {
            Product product = productHome.getInstance();
            product.setName("testproduct");
            product.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.");
            product.setCategory(category);
            File file = fileHome.getInstance();
            file.setImage(byteFromFile);
            file.setImageContentType("image/gif");
            file.setProduct(product);
            product.addFile(file);
            fileHome.persist();
            productHome.persist();
        }
    }

    private void createPages() {
        Page omoss = pageHome.getInstance();
        omoss.setTitle("Om oss");
        omoss.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas quis nisl urna. Vivamus mollis accumsan ligula eget lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam porttitor odio nec elit tincidunt iaculis. Phasellus non eros leo. Quisque turpis tortor, facilisis eu mattis sit amet, suscipit ut est. Nunc ut neque orci. In laoreet sapien nec purus condimentum facilisis. Nulla urna augue, sodales egestas lobortis a, ultricies vestibulum quam. Nunc vitae dolor tellus. Suspendisse ut tellus vitae lorem scelerisque fermentum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut eleifend nisl.");
        pageHome.persist();
        pageHome.clearInstance();
        Page vareButikker = pageHome.getInstance();
        vareButikker.setTitle("Våre butikker");
        vareButikker.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas quis nisl urna. Vivamus mollis accumsan ligula eget lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam porttitor odio nec elit tincidunt iaculis. Phasellus non eros leo. Quisque turpis tortor, facilisis eu mattis sit amet, suscipit ut est. Nunc ut neque orci. In laoreet sapien nec purus condimentum facilisis. Nulla urna augue, sodales egestas lobortis a, ultricies vestibulum quam. Nunc vitae dolor tellus. Suspendisse ut tellus vitae lorem scelerisque fermentum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut eleifend nisl.");
        pageHome.persist();
        pageHome.clearInstance();
        Page lamb = pageHome.getInstance();
        lamb.setTitle("Lambertseter");
        lamb.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas quis nisl urna. Vivamus mollis accumsan ligula eget lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam porttitor odio nec elit tincidunt iaculis. Phasellus non eros leo. Quisque turpis tortor, facilisis eu mattis sit amet, suscipit ut est. Nunc ut neque orci. In laoreet sapien nec purus condimentum facilisis. Nulla urna augue, sodales egestas lobortis a, ultricies vestibulum quam. Nunc vitae dolor tellus. Suspendisse ut tellus vitae lorem scelerisque fermentum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut eleifend nisl.");
        lamb.setParent(vareButikker);
        pageHome.persist();
        pageHome.clearInstance();
        Page drobak = pageHome.getInstance();
        drobak.setTitle("Drøbak");
        drobak.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas quis nisl urna. Vivamus mollis accumsan ligula eget lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam porttitor odio nec elit tincidunt iaculis. Phasellus non eros leo. Quisque turpis tortor, facilisis eu mattis sit amet, suscipit ut est. Nunc ut neque orci. In laoreet sapien nec purus condimentum facilisis. Nulla urna augue, sodales egestas lobortis a, ultricies vestibulum quam. Nunc vitae dolor tellus. Suspendisse ut tellus vitae lorem scelerisque fermentum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut eleifend nisl.");
        drobak.setParent(vareButikker);
        pageHome.persist();
        pageHome.clearInstance();
        Page askim = pageHome.getInstance();
        askim.setTitle("Askim");
        askim.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Maecenas quis nisl urna. Vivamus mollis accumsan ligula eget lobortis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Etiam porttitor odio nec elit tincidunt iaculis. Phasellus non eros leo. Quisque turpis tortor, facilisis eu mattis sit amet, suscipit ut est. Nunc ut neque orci. In laoreet sapien nec purus condimentum facilisis. Nulla urna augue, sodales egestas lobortis a, ultricies vestibulum quam. Nunc vitae dolor tellus. Suspendisse ut tellus vitae lorem scelerisque fermentum. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut eleifend nisl.");
        askim.setParent(vareButikker);
        pageHome.persist();
    }

    private void createUsers() {
        User admin = userHome.getInstance();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoleType(RoleType.ADMIN);
        userHome.persist();
        userHome.clearInstance();
        User olaNordmann = userHome.getInstance();
        olaNordmann.setUsername("ola");
        olaNordmann.setPassword("ola123");
        olaNordmann.setRoleType(RoleType.CUSTOMER);
        userHome.persist();
    }
}
