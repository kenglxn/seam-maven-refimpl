package no.knowit.seam.openejb.mock;

import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Stateless
@Name("entityManagerFactory") 
public class SeamManagedEntityManagerFactoryBean implements SeamManagedEntityManagerFactory {


	@PersistenceUnit //(name = "movie-unit")
	EntityManagerFactory factory;

	@Unwrap
	public EntityManagerFactory getEntityMangagerFactory() {
		return factory;
	}
}