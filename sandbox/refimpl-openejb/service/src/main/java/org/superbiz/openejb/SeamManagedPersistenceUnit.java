package org.superbiz.openejb;

import javax.ejb.Local;
import javax.persistence.EntityManagerFactory;

@Local
public interface SeamManagedPersistenceUnit {
	EntityManagerFactory getEntityMangagerFactory();
}