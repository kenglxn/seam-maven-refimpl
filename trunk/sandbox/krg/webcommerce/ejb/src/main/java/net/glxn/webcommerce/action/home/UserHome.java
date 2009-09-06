package net.glxn.webcommerce.action.home;

import net.glxn.webcommerce.model.User;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("userHome")
public class UserHome extends EntityHome<User> {
    private static final long serialVersionUID = 5875984610758127173L;
}
