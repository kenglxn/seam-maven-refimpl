package net.glxn.webcommerce.util;

import net.glxn.webcommerce.action.home.CategoryHome;
import net.glxn.webcommerce.action.home.FileHome;
import net.glxn.webcommerce.action.home.PageHome;
import net.glxn.webcommerce.action.home.ProductHome;
import net.glxn.webcommerce.action.home.UserHome;
import net.glxn.webcommerce.action.home.SettingsHome;
import net.glxn.webcommerce.action.list.UserList;
import net.glxn.webcommerce.action.list.SettingsList;
import net.glxn.webcommerce.action.upload.FileUtil;
import net.glxn.webcommerce.model.Category;
import net.glxn.webcommerce.model.File;
import net.glxn.webcommerce.model.Page;
import net.glxn.webcommerce.model.Product;
import net.glxn.webcommerce.model.User;
import net.glxn.webcommerce.model.ImageByte;
import net.glxn.webcommerce.model.Settings;
import net.glxn.webcommerce.model.enums.RoleType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Install(debug = true)
@Name("dataPopulator")
public class DataPopulator {

    @Logger
    private Log log;

    @In(create = true)
    UserList userList;

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

    @In(create = true)
    SettingsHome settingsHome;

    @In(create = true)
    SettingsList settingsList;

    private final String imgBase = "C:\\Users\\ken\\Pictures\\devimg\\";

    @Observer("org.jboss.seam.postInitialization")
    public void observe() {
        log.info("dataPopulator observer running");
        if (userList.getResultList().size() > 0) {
            log.info("data already initialized, skipping");
            return;
        }
        Settings settings = settingsHome.getInstance();
        settings.setFilePathServer("D:\\dev\\files\\");
        settings.setFilePathClient("http://localhost/dev/files/");
        settingsHome.persist();
        createUsers();
        createPages();
        createCategoryAndProducts();
    }

    private void createCategoryAndProducts() {
        for (int c = 0; c < 5; c++) {
            Category category = createCategory(c);
            List<Category> childCats = new ArrayList<Category>();
            for (int child = 0; child < 5; child++) {
                categoryHome.clearInstance();
                Category childCat = categoryHome.getInstance();
                childCat.setParent(category);
                childCat.setName("childCategory " + (c + 1));
                categoryHome.persist();
                createProducts(c, childCat);
                childCats.add(childCat);
            }
            categoryHome.clearInstance();
            categoryHome.setInstance(category);
            category.setChildren(childCats);
            categoryHome.update();
            createProducts(c, category);
        }
    }

    private void createProducts(int c, Category category) {
        for (int i = 0; i < 5; i++) {
            createProducts(c + 1, i, category);
        }
    }

    private Category createCategory(int c) {
        categoryHome.clearInstance();
        Category category = categoryHome.getInstance();
        category.setName("category " + (c + 1));
        categoryHome.persist();
        return category;
    }

    private void createProducts(int c, int i, Category category) {
        int iter = i + 1;

        java.io.File image = new java.io.File(imgBase + iter + ".jpg");
        byte[] byteFromFile = new byte[0];
        try {
            byteFromFile = FileUtil.getByteFromFile(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (byteFromFile.length > 0) {
            productHome.clearInstance();
            Product product = productHome.getInstance();
            product.setName(c + "product" + iter);
            product.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin at erat mi, at cursus orci. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam in tellus dolor. Nulla erat tortor, pulvinar tincidunt pulvinar eu, pulvinar id augue. Sed id arcu tellus. Vivamus fermentum fermentum hendrerit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.");
            product.setPrice(iter + 200);
            product.setCategory(category);
            fileHome.clearInstance();
            Settings settings = settingsList.getSingleResult();
            String filepath = settings.getFilePathServer();
            FileUtil.writeToDisk(byteFromFile, filepath.concat(image.getName()));
            File file = fileHome.getInstance();
            file.setFileName(image.getName());
            file.setOriginalByte(new ImageByte(byteFromFile));
            byte[] croppedImage = FileUtil.cropImage(byteFromFile);
            file.setCroppedByte(new ImageByte(croppedImage));
            file.setImageContentType("image/jpeg");
            product.addFile(file);
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
