package no.knowit.seam.example.security;

import no.knowit.seam.example.model.User;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Name("authenticator")
public class Authenticator {

    @Logger
    Log log;
    @In
    private EntityManager entityManager;
    @In
    Identity identity;
    @In
    Credentials credentials;
    @In
    StatusMessages statusMessages;
    @In
    PasswordHash passwordHash;

    /**
     * Check user with given username and password exists in database.
     *
     * @return true if user exist and account is active; false otherwise
     */
    public boolean authenticate() {
        String passwordDigest = passwordHash.generateHash(credentials.getPassword());
        try {
            User user = (User) entityManager.createQuery("from User where active = true and username=:username and passwordDigest=:password")
                .setParameter("username", credentials.getUsername()).setParameter("password", passwordDigest).getSingleResult();
            log.info("#0 has logged in", user.getUsername());
            return true;
        } catch (NoResultException ex) {
            return false;
        }
    }
}
