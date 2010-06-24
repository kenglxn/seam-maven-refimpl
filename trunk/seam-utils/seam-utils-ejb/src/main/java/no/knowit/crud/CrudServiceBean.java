/**
This file is part of javaee-patterns.

javaee-patterns is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

javaee-patterns is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.opensource.org/licenses/gpl-2.0.php>.

 * Copyright (c) 22. June 2009 Adam Bien, blog.adam-bien.com
 * http://press.adam-bien.com
 * 
 * Modified by Leif Olsen
 *   Added a lot of code from Crank, the Java Framework for CRUD and Validation: http://code.google.com/p/krank/
 *   Actually added some code of my own :-)
 */
package no.knowit.crud;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import no.knowit.util.ReflectionUtils;

import org.apache.log4j.Logger;

/**
 * An implementation of the generic CrudService.<br/>
 * 
 * @author http://code.google.com/p/krank/
 * @author adam-bien.com
 * @author Leif Olsen
 */
@Stateless(name = CrudService.NAME)
public class CrudServiceBean implements CrudService {
  private static final Logger log = Logger.getLogger(CrudServiceBean.class);
  private static final String PARAM_NOT_NULL = "The \"%s\" parameter can not be null";

	@PersistenceContext
	protected EntityManager entityManager;

	// 'C'
	public <T> T persist(T entity) {
		getEntityManager().persist(entity);
		getEntityManager().flush();
		getEntityManager().refresh(entity);
		return entity;
	}

	public <T> Collection<T> persist(Collection<T> entities) {
		if(entities != null) {
	    Collection<T> persistedResults = new ArrayList<T>(entities.size());
  		for (T entity : entities) {
  			persistedResults.add(persist(entity));
  		}
      return persistedResults;
		}
		return null;
	}

	// 'R'
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T> T find(Class<T> entityClass, Object id) {
		return (T) getEntityManager().find(entityClass, id);
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass) {
		return getEntityManager().createQuery(CrudServiceUtils.createSelectJpql(entityClass)).getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
	public <T> List<T> find(Class<T> entityClass, int startPosition, int maxResult) {
		return getEntityManager().createQuery(CrudServiceUtils.createSelectJpql(entityClass))
			.setFirstResult(startPosition).setMaxResults(maxResult).getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
	public <T> List<T> find(T example, boolean distinct, boolean any) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query.getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
	public <T> List<T> find(T example, boolean distinct, boolean any,	int startPosition, int maxResult) {
		Query query = createExampleQuery(example, true, distinct, any);
		return query.setFirstResult(startPosition).setMaxResults(maxResult).getResultList();
	}

	// 'U'
	public <T> T merge(T entity) {
		return (T) getEntityManager().merge(entity);
	}

	public <T> Collection<T> merge(Collection<T> entities) {
		if(entities == null) {
		  throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
		}
		Collection<T> mergedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			mergedResults.add(merge(entity));
		}
		return mergedResults;
	}

	// 'D'
	public void remove(final Object entity) {
		EntityManager em = getEntityManager();
		Object managedEntity = em.contains(entity) ? entity : em.merge(entity);
		em.remove(managedEntity);
	}

  public void remove(Class<?> entityClass) {
    getEntityManager().createQuery(CrudServiceUtils.createDeleteJpql(entityClass)).executeUpdate();
  }
  
	public void remove(Class<?> entityClass, Object id) {
		Object ref = getEntityManager().getReference(entityClass, id);
		getEntityManager().remove(ref);
	}

	public void remove(Collection<Object> entities) {
    if(entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
		for (Object entity : entities) {
			remove(entity);
		}
	}

	public void remove(final Object example, boolean any) {
		Query query = createExampleQuery(example, false, false, any);
		query.executeUpdate();
	}
	
	// C or U :-)
	public <T> T store(T entity) {
    if(entity == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entity"));
    }
		//Object id = getIdentity(entity);
		Object id = CrudServiceUtils.getIdValues(entity).get(0);
		if (!log.isDebugEnabled()) {
			if (id != null) {
				return find(entity.getClass(), id) == null ? persist(entity) : merge(entity);
			} 
			else {
				return persist(entity);
			}
		}
		
		// Debug
		if (id != null) {
			if (find(entity.getClass(), id) == null) {
				T e = persist(entity);
				log.debug("CrudService.store: persisted new entity, got id: "	+ 
				    CrudServiceUtils.getIdValues(entity).get(0));
				return e;
			} else {
				log.debug("CrudService.store: merge existing entity with id: " + id);
				return merge(entity);
			}
		} 
		else {
			T e = persist(entity);
			log.debug("CrudService.store: persisted new entity, got id: "	+ 
			    CrudServiceUtils.getIdValues(entity).get(0));
			return e;
		}
	}

	public <T> Collection<T> store(Collection<T> entities) {
    if(entities == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
    Collection<T> storedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			storedResults.add(store(entity));
		}
		return storedResults;
	}
  // end C.R.U.D

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int count(final Class<?> entityClass) {
    Long result = (Long) getEntityManager().createQuery(
        CrudServiceUtils.createCountJpql(entityClass)).getSingleResult();

    return result.intValue();
	}
	
  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T> T refresh(T transientEntity) {
		EntityManager em = getEntityManager();
		T managedEntity = em.contains(transientEntity) ? transientEntity : em.merge(transientEntity);
		em.refresh(managedEntity);
		return managedEntity;
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T> T refresh(Class<T> entityClass, Object id) {
		EntityManager em = getEntityManager();
		T managedEntity = em.find(entityClass, id);
		em.refresh(managedEntity);
		return managedEntity;
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T> Collection<T> refresh(Collection<T> entities) {
    if(entities == null) { 
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "entities"));
    }
		Collection<T> refreshedResults = new ArrayList<T>(entities.size());
		for (T entity : entities) {
			refreshedResults.add(refresh(entity));
		}
		return refreshedResults;
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isManaged(Object entity) {
		return entity != null && getEntityManager().contains(entity);
	}

	public void flush() {
		getEntityManager().flush();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void clear() {
		getEntityManager().clear();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void flushAndClear() {
		EntityManager em = getEntityManager();
		em.flush();
		em.clear();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName) {
		return this.entityManager.createNamedQuery(namedQueryName).getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters) {
		return findWithNamedQuery(namedQueryName, parameters, 0);
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String queryName, int resultLimit) {
		return this.entityManager.createNamedQuery(queryName).setMaxResults(resultLimit).getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
  public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		Query query = this.entityManager.createNamedQuery(namedQueryName);
		if (resultLimit > 0) {
			query.setMaxResults(resultLimit);
		}
		for (Entry<String, Object> entry : rawParameters) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query.getResultList();
	}

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
  public <T> List<T> findByNativeQuery(String sql, Class<T> type) {
		return this.entityManager.createNativeQuery(sql, type).getResultList();
	}

	/**
	 * @return the entitymanager wired to this service
	 * @throws java.lang.IllegalStateException
	 *           if EntityManager has not been set on this service before usage
	 */
	protected EntityManager getEntityManager() {
		if (entityManager == null) {
			throw new IllegalStateException("EntityManager has not been set on service before usage");
		}
		return entityManager;
	}

	/**
	 * This is a simple form of query by example. Produces a SELECT or DELETE
	 * query based on non null property values in the <code>example</code>
	 * parameter
	 * 
	 * The limitations are:
	 * <ul>
	 *   <li>The entity must have at least one @Id annotation</li>
	 *   <li>Only primitives can be part of the query(Integer, String etc.)
	 *   <li>Can not handle embedded classes, object references, arrays and collections while generating JPQL</li>
	 * </ul>
	 * 
	 */
	protected Query createExampleQuery(final Object example, boolean select, boolean distinct, boolean any) {
    if(example == null) {
      throw new IllegalArgumentException(String.format(PARAM_NOT_NULL, "example"));
    }
    String jpql = CrudServiceUtils.createJpql(example, select, distinct, any);
    Map<String, Member> attributes = CrudServiceUtils.findQueryableAttributes(example.getClass());
    attributes = CrudServiceUtils.reduceQueryableAttributesToPopulatedFields(example, attributes);

    final StringBuilder debugData = new StringBuilder();
    
    if(log.isDebugEnabled()) {
      debugData.append(example.getClass().getSimpleName() + " class query parameters: ");
    }
    
    Query query = getEntityManager().createQuery(jpql);
    Set<Entry<String, Member>> properties = attributes.entrySet();
    for (Entry<String, Member> entry : properties) {
      String property = entry.getKey();
      Member member = entry.getValue();
      Object value = member != null ? ReflectionUtils.get(member, example) : null;
      query.setParameter(property, value);
      
      if(log.isDebugEnabled()) {
        debugData.append("[:" + property + " = " + value + "] ");
      }
    } 

    if(log.isDebugEnabled()) {
      log.debug(debugData);
    }
    
		return query;
	}
	
}
