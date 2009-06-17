package org.jboss.seam.example.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.example.entity.User;

@Name("userList")
public class UserList extends EntityQuery<User>
{
    public UserList()
    {
        setEjbql("select user from User user");
    }
}
