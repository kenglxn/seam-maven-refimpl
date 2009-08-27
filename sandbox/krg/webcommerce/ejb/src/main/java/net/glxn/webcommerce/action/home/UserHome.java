package net.glxn.webcommerce.action.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import net.glxn.webcommerce.model.User;

@Name("userHome")
public class UserHome extends EntityHome<User> {
    private static final long serialVersionUID = 5875984610758127173L;
}
