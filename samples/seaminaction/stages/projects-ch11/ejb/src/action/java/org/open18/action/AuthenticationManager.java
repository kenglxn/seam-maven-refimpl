package org.open18.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.open18.auth.PasswordManager;
import org.open18.model.Golfer;
import org.open18.model.Member;
import org.open18.model.Role;


@Name("authenticationManager")
public class AuthenticationManager {

	@Logger private Log log;
	@In private EntityManager entityManager;
	
	@In private Identity identity;
    @In Credentials credentials;
    
	@In private PasswordManager passwordManager;
	@Out(required = false) private Golfer currentGolfer;

	@Transactional public boolean authenticate() {
		
		log.info("authenticating {0}", credentials.getUsername());
		try {
			Member member = (Member) entityManager.createQuery(
				//"select m from Member m where m.username = :username")
				"select distinct m from Member m left join fetch m.roles where m.username = :username")
				.setParameter("username", credentials.getUsername())
				.getSingleResult();

			if (!validatePassword(credentials.getPassword(), member)) {
				return false;
			}

			identity.addRole("member");
			if (member.getRoles() != null) {
				for (Role role : member.getRoles()) {
					identity.addRole(role.getName());
				}
			}

			if (member instanceof Golfer) {
				currentGolfer = (Golfer) member;
				identity.addRole("golfer");
			}
			return true;
		} catch (NoResultException e) {
			return false;
		}
		
		/*
        log.info("authenticating {0}", credentials.getUsername());
        //write your authentication logic here,
        //return true if the authentication was
        //successful, false otherwise...
        if ("admin".equals(credentials.getUsername()))
        {
            identity.addRole("admin");
            return true;
        }
        return false;
		*/
	}

	public boolean validatePassword(String password, Member m) {
		return passwordManager.hash(password).equals(m.getPasswordHash());
	}
}
