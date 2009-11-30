package no.knowit.seam.openejb.mock;

import javax.ejb.Local;
import javax.persistence.EntityManagerFactory;

@Local
public interface SeamManagedEntityManagerFactory {
	EntityManagerFactory getEntityMangagerFactory();
}