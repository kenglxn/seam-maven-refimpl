package net.glxn.webcommerce.action.list;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import net.glxn.webcommerce.model.User;

@Name("userList")
public class UserList extends EntityQuery<User>
{
    public UserList()
    {
        setEjbql("select user from User user");
    }
}
