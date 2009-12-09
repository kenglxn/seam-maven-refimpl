package no.knowit.seam.openejb.mock;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;


/**
 * Copied from http://www.jboss.org/index.html?module=bb&op=viewtopic&p=4177645
 *
 */

@Stateless
@Name("entityManagerFactory") 
public class SeamManagedEntityManagerFactoryBean implements SeamManagedEntityManagerFactory {
	
	private static final LogProvider log = Logging.getLogProvider(SeamManagedEntityManagerFactoryBean.class); 
	
	@PersistenceUnit(name="openejb-unit")
	EntityManagerFactory entityManagerFactory;

	@Unwrap
	public EntityManagerFactory getEntityMangagerFactory() {
		
		log.debug("**** @Unwrap -> SeamManagedEntityManagerFactoryBean.getEntityMangagerFactory");
		return entityManagerFactory;
	}
}