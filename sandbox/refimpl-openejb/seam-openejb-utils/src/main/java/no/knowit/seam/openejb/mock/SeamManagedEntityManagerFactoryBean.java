package no.knowit.seam.openejb.mock;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Copied from http://www.jboss.org/index.html?module=bb&op=viewtopic&p=4177645
 *
 */

@Stateless
@Name("entityManagerFactory") 
@Scope(ScopeType.STATELESS)
public class SeamManagedEntityManagerFactoryBean implements SeamManagedEntityManagerFactory {


	@PersistenceUnit
	EntityManagerFactory factory;

	@Unwrap
	public EntityManagerFactory getEntityMangagerFactory() {
		return factory;
	}
}