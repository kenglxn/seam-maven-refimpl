package org.superbiz.openejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Stateless
@Name("entityManagerFactory") 
public class SeamManagedPersistenceUnitImpl implements SeamManagedPersistenceUnit {


	@PersistenceUnit(name = "movie-unit")
	EntityManagerFactory factory;

	@PostConstruct
	protected void postConstruct() {
		System.out.println("**********************@PostConstruct->SeamManagedPersistenceUnit.postConstruct");
	}
	
	@Unwrap
	public EntityManagerFactory getEntityMangagerFactory() {
		return factory;
	}
}