package net.glxn.webcommerce.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import net.glxn.webcommerce.action.home.UserHome;
import net.glxn.webcommerce.model.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Name("authenticator")
public class Authenticator {
    @Logger
    private Log log;

    @In
    Identity identity;

    @In
    Credentials credentials;

    @In
    private EntityManager entityManager;

    public boolean authenticate() {

        log.info("authenticating {0}", credentials.getUsername());
        String hql = "" +
                "select u from User u " +
                "   where u.username =:username " +
                "       and u.password =:password";
        Query query = entityManager.createQuery(hql);
        query.setParameter("username", credentials.getUsername());
        query.setParameter("password", credentials.getPassword());
        User user = (User) query.getSingleResult();
        if (user == null) {
            return false;
        }
        String role = user.getRoleType().name().toLowerCase();
        log.info("authenticated. role={0}", role);
        identity.addRole(role);
        return true;
    }

}
