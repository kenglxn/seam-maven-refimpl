package net.glxn.webcommerce.action.list;

import net.glxn.webcommerce.model.User;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("userList")
public class UserList extends EntityQuery<User> {
    private static final long serialVersionUID = 4556847973938127040L;

    public UserList() {
        setEjbql("select user from User user");
    }

}
