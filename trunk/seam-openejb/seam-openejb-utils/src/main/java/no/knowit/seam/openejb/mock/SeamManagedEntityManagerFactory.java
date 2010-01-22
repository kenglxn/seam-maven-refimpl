package no.knowit.seam.openejb.mock;

import javax.ejb.Local;
import javax.persistence.EntityManagerFactory;

/**
 * @author <a href="http://community.jboss.org/message/5521#5521">jboss.entity.manager.factory.jndi.name does not publish</a>
 */
@Local
public interface SeamManagedEntityManagerFactory {
	EntityManagerFactory getEntityMangagerFactory();
}